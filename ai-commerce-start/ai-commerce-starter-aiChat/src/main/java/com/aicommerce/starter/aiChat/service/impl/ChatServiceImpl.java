package com.aicommerce.starter.aiChat.service.impl;

import com.aicommerce.starter.aiChat.entity.AiModelEntity;
import com.aicommerce.starter.aiChat.factory.ModelFactory;
import com.aicommerce.starter.aiChat.mapper.AiModelMapper;
import com.aicommerce.starter.aiChat.service.ChatService;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;


/**
 * 类名: ChatServiceImpl
 * 描述:
 * 作者: xuzeyu
 * 创建时间: 2026/7/21
 */
@Component
public class ChatServiceImpl implements ChatService {

    @Resource
    private  ModelFactory modelFactory;

    @Resource
    private  AiModelMapper aiModelMapper;

    @Override
    public void chat(Long modelId, String message, SseEmitter emitter) {
        AiModelEntity model = aiModelMapper.selectById(modelId);

        StreamingChatModel chatModel =
                modelFactory.create(model);

        chatModel.chat(message,  new StreamingChatResponseHandler(){

                    @Override
                    public void onPartialResponse(String token){

                        try {
                            emitter.send(token);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onCompleteResponse(ChatResponse response){

                        emitter.complete();

                    }

                    @Override
                    public void onError(Throwable error){

                        emitter.completeWithError(error);

                    }

                }
        );

    }
}
