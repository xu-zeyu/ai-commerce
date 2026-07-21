package com.aicommerce.starter.aiChat.factory;

import com.aicommerce.starter.aiChat.entity.AiModelEntity;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

/**
 * 类名: ModelFactory
 * 描述: 核心
 * 作者: xuzeyu
 * 创建时间: 2026/7/21
 */
@Component
public class ModelFactory {

    public OpenAiChatModel build(AiModelEntity model){

        return OpenAiChatModel.builder()
                .apiKey(model.getApiKey())
                .baseUrl(model.getBaseUrl())
                .modelName(model.getModelName())
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}
