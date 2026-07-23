package com.aicommerce.starter.aiChat.service.impl;

import com.aicommerce.starter.aiChat.dto.response.ChatStreamResponse;
import com.aicommerce.starter.aiChat.entity.AiModelEntity;
import com.aicommerce.starter.aiChat.factory.ModelFactory;
import com.aicommerce.starter.aiChat.mapper.AiModelMapper;
import com.aicommerce.starter.aiChat.model.ChatMemoryId;
import com.aicommerce.starter.aiChat.model.ChatUserTypeEnum;
import com.aicommerce.starter.aiChat.service.AiChatAssistant;
import com.aicommerce.starter.aiChat.service.ChatService;
import com.aicommerce.starter.aiChat.tool.browser.BrowserMcpSession;
import com.aicommerce.starter.aiChat.tool.browser.BrowserMcpToolProviderFactory;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ToolChoice;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * AI 流式聊天服务。
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private static final String MESSAGE_EVENT = "message";
    private static final String DONE_EVENT = "done";
    private static final String ERROR_EVENT = "error";
    private static final String DONE_CONTENT = "[DONE]";
    private static final String STREAM_ERROR_MESSAGE = "AI流式响应失败，请稍后重试";
    private static final String MEMORY_ERROR_MESSAGE = "AI回复已生成，但聊天记忆保存失败";
    private static final String BROWSER_MCP_SYSTEM_MESSAGE = """
            你可以使用 Browser MCP 服务提供的浏览器工具访问和操作网页。
            当用户要求打开、浏览、搜索或操作网页时，应主动选择合适的 Browser MCP 工具完成任务。
            需要操作网页时，必须直接返回协议定义的结构化 tool_calls；禁止用“导航到该网址”等普通文本或JSON代替工具调用。
            页面状态变化后应重新读取当前页面，再继续点击、填写或提取内容。
            不要声称自己无法访问浏览器。
            """;
    private static final Pattern URL_PATTERN = Pattern.compile(
            "(?i)(?:https?://|www\\.)[^\\s]+"
    );
    private static final Pattern BROWSER_INTENT_PATTERN = Pattern.compile(
            "(?i)(?:(?:打开|访问|浏览|查看|导航|跳转|进入|搜索|检索).{0,40}(?:网页|网站|网址|链接|页面)|"
                    + "(?:网页|网站|网址|链接|页面).{0,20}(?:打开|访问|浏览|查看|导航|跳转|进入|搜索|检索)|"
                    + "点击|填写|滚动|截图|抓取|联网搜索|上网搜索|"
                    + "(?:open|visit|browse|navigate|search).{0,40}(?:url|website|webpage|page|link|browser)|"
                    + "(?:click|fill|scroll|screenshot))"
    );

    /**
     * 防止同一用户、同一模型的并发请求互相覆盖记忆。
     */
    private final ConcurrentMap<ChatMemoryId, Object> activeChats = new ConcurrentHashMap<>();

    /**
     * Token估算器是不可变对象，按模型复用可避免每轮聊天重复初始化编码表。
     */
    private final ConcurrentMap<String, TokenCountEstimator> tokenCountEstimators = new ConcurrentHashMap<>();

    @Resource
    private ModelFactory modelFactory;

    @Resource
    private AiModelMapper aiModelMapper;

    @Resource
    private ChatMemoryStore chatMemoryStore;

    @Resource
    private BrowserMcpToolProviderFactory browserMcpToolProviderFactory;

    @Value("${ai.chat.memory.max-tokens:4000}")
    private int maxMemoryTokens;

    @Value("${ai.chat.memory.fallback-tokenizer-model:gpt-4o-mini}")
    private String fallbackTokenizerModel;

    @Override
    public void chat(
            ChatUserTypeEnum userType,
            Long userId,
            Long modelId,
            String sessionId,
            String message,
            SseEmitter emitter) {
        AiModelEntity model = aiModelMapper.selectById(modelId);
        if (model == null) {
            throw new IllegalArgumentException("AI模型不存在，modelId=" + modelId);
        }

        ChatMemoryId memoryId = new ChatMemoryId(userType, userId, modelId, sessionId);
        Object activeChat = new Object();
        if (activeChats.putIfAbsent(memoryId, activeChat) != null) {
            throw new IllegalStateException("上一轮对话尚未完成，请稍后再试");
        }

        AtomicBoolean sseCompleted = new AtomicBoolean(false);
        BrowserMcpSession browserMcpSession;
//        try {
//            browserMcpSession = createBrowserMcpSession();
//        } catch (RuntimeException exception) {
//            releaseChat(memoryId, activeChat);
//            throw exception;
//        }
        try {
            TokenCountEstimator tokenCountEstimator = createTokenCountEstimator(model.getModelName());
            ChatMemory persistentMemory = createMemory(memoryId, tokenCountEstimator);
            ChatMemory requestMemory = createMemory(null, tokenCountEstimator);
            requestMemory.set(persistentMemory.messages());
            StreamingChatModel chatModel = modelFactory.createChatModel(model);
            AiServices<AiChatAssistant> assistantBuilder = AiServices.builder(AiChatAssistant.class)
                    .streamingChatModel(chatModel)
                    .chatMemory(requestMemory);
//            if (browserMcpSession != null) {
//                boolean forceInitialToolCall = shouldForceInitialBrowserToolCall(message);
//                AtomicBoolean initialToolAwareRequest = new AtomicBoolean(true);
//                assistantBuilder.toolProvider(browserMcpSession.getToolProvider())
//                        .systemMessage(BROWSER_MCP_SYSTEM_MESSAGE)
//                        .chatRequestTransformer(request -> configureBrowserToolChoice(
//                                request,
//                                initialToolAwareRequest,
//                                forceInitialToolCall))
//                        .maxToolCallingRoundTrips(browserMcpToolProviderFactory.getMaxToolRoundTrips());
//            }
            AiChatAssistant assistant = assistantBuilder.build();

            // SSE连接可能先于模型及工具调用结束，MCP会话只能由模型流终态关闭。
            emitter.onCompletion(() -> sseCompleted.set(true));
            emitter.onTimeout(() -> completeEmitter(emitter, sseCompleted));
            emitter.onError(error -> {
                sseCompleted.set(true);
                log.debug("SSE连接异常关闭: {}", error.getMessage());
            });

            TokenStream tokenStream = assistant.chat(message);
            tokenStream
                    .onPartialResponse(token -> {
                        if (sseCompleted.get()) {
                            return;
                        }

                        try {
                            sendEvent(emitter, MESSAGE_EVENT, token);
                        } catch (IOException exception) {
                            completeEmitter(emitter, sseCompleted);
                            log.debug("客户端已断开SSE连接: {}", exception.getMessage());
                        }
                    })
                    .onCompleteResponse(response -> {
                        if (!sseCompleted.compareAndSet(false, true)) {
                            releaseChat(memoryId, activeChat);
//                            closeBrowserMcpSession(browserMcpSession);
                            return;
                        }

                        try {
                            persistentMemory.set(requestMemory.messages());
                            sendEvent(emitter, DONE_EVENT, DONE_CONTENT);
                        } catch (IOException exception) {
                            log.debug("发送SSE完成事件失败: {}", exception.getMessage());
                        } catch (RuntimeException exception) {
                            log.error("保存聊天记忆失败，memoryId={}", memoryId, exception);
                            sendErrorEvent(emitter, MEMORY_ERROR_MESSAGE);
                        } finally {
                            releaseChat(memoryId, activeChat);
//                            closeBrowserMcpSession(browserMcpSession);
                            emitter.complete();
                        }
                    })
                    .onError(error -> {
                        boolean shouldNotifyClient = sseCompleted.compareAndSet(false, true);
                        try {
                            log.error("AI流式响应失败", error);
                            if (shouldNotifyClient) {
                                sendErrorEvent(emitter, STREAM_ERROR_MESSAGE);
                            }
                        } finally {
                            releaseChat(memoryId, activeChat);
//                            closeBrowserMcpSession(browserMcpSession);
                            if (shouldNotifyClient) {
                                emitter.complete();
                            }
                        }
                    })
                    .start();
        } catch (RuntimeException exception) {
            releaseChat(memoryId, activeChat);
//            closeBrowserMcpSession(browserMcpSession);
            throw exception;
        }
    }

    private BrowserMcpSession createBrowserMcpSession() {
        if (!browserMcpToolProviderFactory.isEnabled()) {
            return null;
        }
        return browserMcpToolProviderFactory.createSession();
    }

    private boolean shouldForceInitialBrowserToolCall(String message) {
        if (!browserMcpToolProviderFactory.shouldForceToolCallOnBrowserIntent()) {
            return false;
        }
        return message != null
                && (URL_PATTERN.matcher(message).find() || BROWSER_INTENT_PATTERN.matcher(message).find());
    }

    private ChatRequest configureBrowserToolChoice(
            ChatRequest request,
            AtomicBoolean initialToolAwareRequest,
            boolean forceInitialToolCall) {
        if (!forceInitialToolCall) {
            return request;
        }

        return request.toBuilder().toolChoice(ToolChoice.AUTO).build();
    }

    private ChatMemory createMemory(
            ChatMemoryId memoryId,
            TokenCountEstimator tokenCountEstimator) {
        TokenWindowChatMemory.Builder builder = TokenWindowChatMemory.builder()
                .maxTokens(maxMemoryTokens, tokenCountEstimator);
        if (memoryId != null) {
            builder.id(memoryId).chatMemoryStore(chatMemoryStore);
        }
        return builder.build();
    }

    private TokenCountEstimator createTokenCountEstimator(String modelName) {
        if (modelName == null || modelName.isBlank()) {
            throw new IllegalArgumentException("AI模型名称不能为空");
        }

        return tokenCountEstimators.computeIfAbsent(modelName, this::createTokenCountEstimatorInternal);
    }

    private TokenCountEstimator createTokenCountEstimatorInternal(String modelName) {
        try {
            return new OpenAiTokenCountEstimator(modelName);
        } catch (IllegalArgumentException exception) {
            log.warn(
                    "模型 {} 没有可用的本地Token编码器，聊天记忆将使用 {} 近似估算",
                    modelName,
                    fallbackTokenizerModel);
            try {
                return new OpenAiTokenCountEstimator(fallbackTokenizerModel);
            } catch (IllegalArgumentException fallbackException) {
                throw new IllegalStateException(
                        "聊天记忆回退Token编码器配置无效: " + fallbackTokenizerModel,
                        fallbackException);
            }
        }
    }

    private void sendEvent(SseEmitter emitter, String eventName, String content) throws IOException {
        emitter.send(SseEmitter.event()
                .name(eventName)
                .data(new ChatStreamResponse(content), MediaType.APPLICATION_JSON));
    }

    private void sendErrorEvent(SseEmitter emitter, String message) {
        try {
            sendEvent(emitter, ERROR_EVENT, message);
        } catch (IOException exception) {
            log.debug("发送SSE错误事件失败: {}", exception.getMessage());
        }
    }

    private void completeEmitter(SseEmitter emitter, AtomicBoolean sseCompleted) {
        if (sseCompleted.compareAndSet(false, true)) {
            emitter.complete();
        }
    }

    private void closeBrowserMcpSession(BrowserMcpSession browserMcpSession) {
        if (browserMcpSession != null) {
            browserMcpSession.close();
        }
    }

    private void releaseChat(ChatMemoryId memoryId, Object activeChat) {
        activeChats.remove(memoryId, activeChat);
    }
}
