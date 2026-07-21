package com.aicommerce.starter.aiChat.service.impl;

import com.aicommerce.starter.aiChat.dto.response.ChatStreamResponse;
import com.aicommerce.starter.aiChat.entity.AiModelEntity;
import com.aicommerce.starter.aiChat.factory.ModelFactory;
import com.aicommerce.starter.aiChat.mapper.AiModelMapper;
import com.aicommerce.starter.aiChat.service.ChatService;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 类名: ChatServiceImpl
 * 描述:
 * 作者: xuzeyu
 * 创建时间: 2026/7/21
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private static final String MESSAGE_EVENT = "message";
    private static final String DONE_EVENT = "done";
    private static final String ERROR_EVENT = "error";
    private static final String DONE_CONTENT = "[DONE]";
    private static final String STREAM_ERROR_MESSAGE = "AI流式响应失败，请稍后重试";

    @Resource
    private ModelFactory modelFactory;

    @Resource
    private AiModelMapper aiModelMapper;

    @Override
    public void chat(Long modelId, String message, SseEmitter emitter) {
        AiModelEntity model = aiModelMapper.selectById(modelId);
        if (model == null) {
            throw new IllegalArgumentException("AI模型不存在，modelId=" + modelId);
        }

        StreamingChatModel chatModel = modelFactory.create(model);
        AtomicBoolean completed = new AtomicBoolean(false);

        emitter.onCompletion(() -> completed.set(true));
        emitter.onTimeout(() -> completeEmitter(emitter, completed));
        emitter.onError(error -> {
            completed.set(true);
            log.debug("SSE连接异常关闭: {}", error.getMessage());
        });

        chatModel.chat(message, new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String token) {
                if (completed.get()) {
                    return;
                }

                try {
                    sendEvent(emitter, MESSAGE_EVENT, token);
                } catch (IOException e) {
                    completed.set(true);
                    log.debug("客户端已断开SSE连接: {}", e.getMessage());
                }
            }

            @Override
            public void onCompleteResponse(ChatResponse response) {
                if (completed.compareAndSet(false, true)) {
                    try {
                        sendEvent(emitter, DONE_EVENT, DONE_CONTENT);
                    } catch (IOException e) {
                        log.debug("发送SSE完成事件失败: {}", e.getMessage());
                    } finally {
                        emitter.complete();
                    }
                }
            }

            @Override
            public void onError(Throwable error) {
                if (completed.compareAndSet(false, true)) {
                    log.error("AI流式响应失败", error);
                    try {
                        sendEvent(emitter, ERROR_EVENT, STREAM_ERROR_MESSAGE);
                    } catch (IOException e) {
                        log.debug("发送SSE错误事件失败: {}", e.getMessage());
                    } finally {
                        emitter.complete();
                    }
                }
            }
        });
    }

    private void sendEvent(SseEmitter emitter, String eventName, String content) throws IOException {
        emitter.send(SseEmitter.event()
                .name(eventName)
                .data(new ChatStreamResponse(content), MediaType.APPLICATION_JSON));
    }

    private void completeEmitter(SseEmitter emitter, AtomicBoolean completed) {
        if (completed.compareAndSet(false, true)) {
            emitter.complete();
        }
    }
}
