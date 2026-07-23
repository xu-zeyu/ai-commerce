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
            页面状态变化后应重新读取当前页面，再继续点击、填写或提取内容。
            不要声称自己无法访问浏览器。
            """;

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

        AtomicBoolean completed = new AtomicBoolean(false);
        BrowserMcpSession browserMcpSession;
        try {
            browserMcpSession = createBrowserMcpSession();
        } catch (RuntimeException exception) {
            releaseChat(memoryId, activeChat);
            throw exception;
        }
        try {
            TokenCountEstimator tokenCountEstimator = createTokenCountEstimator(model.getModelName());
            ChatMemory persistentMemory = createMemory(memoryId, tokenCountEstimator);
            ChatMemory requestMemory = createMemory(null, tokenCountEstimator);
            requestMemory.set(persistentMemory.messages());
            StreamingChatModel chatModel = modelFactory.createChatModel(model);
            AiServices<AiChatAssistant> assistantBuilder = AiServices.builder(AiChatAssistant.class)
                    .streamingChatModel(chatModel)
                    .chatMemory(requestMemory);
            if (browserMcpSession != null) {
                assistantBuilder.toolProvider(browserMcpSession.getToolProvider())
                        .systemMessage(BROWSER_MCP_SYSTEM_MESSAGE)
                        .maxToolCallingRoundTrips(browserMcpToolProviderFactory.getMaxToolRoundTrips());
            }
            AiChatAssistant assistant = assistantBuilder.build();

            emitter.onCompletion(() -> {
                completed.set(true);
                releaseChat(memoryId, activeChat);
                closeBrowserMcpSession(browserMcpSession);
            });
            emitter.onTimeout(() -> completeEmitter(
                    emitter,
                    completed,
                    memoryId,
                    activeChat,
                    browserMcpSession));
            emitter.onError(error -> {
                completed.set(true);
                releaseChat(memoryId, activeChat);
                closeBrowserMcpSession(browserMcpSession);
                log.debug("SSE连接异常关闭: {}", error.getMessage());
            });

            TokenStream tokenStream = assistant.chat(message);
            tokenStream
                    .onPartialResponse(token -> {
                        if (completed.get()) {
                            return;
                        }

                        try {
                            sendEvent(emitter, MESSAGE_EVENT, token);
                        } catch (IOException exception) {
                            completeEmitter(
                                    emitter,
                                    completed,
                                    memoryId,
                                    activeChat,
                                    browserMcpSession);
                            log.debug("客户端已断开SSE连接: {}", exception.getMessage());
                        }
                    })
                    .onCompleteResponse(response -> {
                        if (!completed.compareAndSet(false, true)) {
                            releaseChat(memoryId, activeChat);
                            closeBrowserMcpSession(browserMcpSession);
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
                            closeBrowserMcpSession(browserMcpSession);
                            emitter.complete();
                        }
                    })
                    .onError(error -> {
                        if (completed.compareAndSet(false, true)) {
                            log.error("AI流式响应失败", error);
                            sendErrorEvent(emitter, STREAM_ERROR_MESSAGE);
                            releaseChat(memoryId, activeChat);
                            closeBrowserMcpSession(browserMcpSession);
                            emitter.complete();
                        }
                    })
                    .start();
        } catch (RuntimeException exception) {
            releaseChat(memoryId, activeChat);
            closeBrowserMcpSession(browserMcpSession);
            throw exception;
        }
    }

    private BrowserMcpSession createBrowserMcpSession() {
        if (!browserMcpToolProviderFactory.isEnabled()) {
            return null;
        }
        return browserMcpToolProviderFactory.createSession();
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

    private void completeEmitter(
            SseEmitter emitter,
            AtomicBoolean completed,
            ChatMemoryId memoryId,
            Object activeChat,
            BrowserMcpSession browserMcpSession) {
        if (completed.compareAndSet(false, true)) {
            releaseChat(memoryId, activeChat);
            closeBrowserMcpSession(browserMcpSession);
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
