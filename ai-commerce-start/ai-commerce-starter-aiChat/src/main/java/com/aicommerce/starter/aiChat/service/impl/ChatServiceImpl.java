package com.aicommerce.starter.aiChat.service.impl;

import com.aicommerce.starter.aiChat.entity.AiModelEntity;
import com.aicommerce.starter.aiChat.factory.ModelFactory;
import com.aicommerce.starter.aiChat.mapper.AiModelMapper;
import com.aicommerce.starter.aiChat.service.ChatService;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

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
    public String chat(Long modelId, String message) {
        AiModelEntity model = aiModelMapper.selectById(modelId);

        OpenAiChatModel chatModel =
                modelFactory.build(model);

        return chatModel.chat(message);
    }
}
