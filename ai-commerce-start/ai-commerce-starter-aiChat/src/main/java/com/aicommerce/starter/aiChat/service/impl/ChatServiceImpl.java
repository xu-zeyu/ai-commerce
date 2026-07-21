package com.aicommerce.starter.aiChat.service.impl;

import com.aicommerce.starter.aiChat.entity.AiModelEntity;
import com.aicommerce.starter.aiChat.factory.ModelFactory;
import com.aicommerce.starter.aiChat.mapper.AiModelMapper;
import com.aicommerce.starter.aiChat.service.ChatService;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
                    emitter.send(SseEmitter.event().data(token));
                } catch (IOException e) {
                    completed.set(true);
                    log.debug("客户端已断开SSE连接: {}", e.getMessage());
                }
            }

            @Override
            public void onCompleteResponse(ChatResponse response) {
                completeEmitter(emitter, completed);
            }

            @Override
            public void onError(Throwable error) {
                if (completed.compareAndSet(false, true)) {
                    log.error("AI流式响应失败", error);
                    emitter.completeWithError(error);
                }
            }
        });
    }

    private void completeEmitter(SseEmitter emitter, AtomicBoolean completed) {
        if (completed.compareAndSet(false, true)) {
            emitter.complete();
        }
    }
}
