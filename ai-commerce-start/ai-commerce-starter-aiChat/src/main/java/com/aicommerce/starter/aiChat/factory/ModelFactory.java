package com.aicommerce.starter.aiChat.factory;

import com.aicommerce.starter.aiChat.entity.AiModelEntity;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 类名: ModelFactory
 * 描述: 核心
 * 作者: xuzeyu
 * 创建时间: 2026/7/21
 */
@Component
public class ModelFactory {

    /*
    *   apiKey  模型提供的 key
    *   baseUrl  模型请求基地址
    *   modelName 模型具体名称
    *   logRequests/logResponses  是否打印 输入/输出 信息
    * */

    public StreamingChatModel createChatModel(AiModelEntity model) {

        return OpenAiStreamingChatModel.builder()
                .apiKey(model.getApiKey())
                .baseUrl(model.getBaseUrl())
                .modelName(model.getModelName())
                .logRequests(true)
                .logResponses(true)
                .build();
    }


    public ImageModel createImageModel(AiModelEntity model) {
        return OpenAiImageModel.builder()
                .apiKey(model.getApiKey())
                .baseUrl(model.getBaseUrl())
                .modelName(model.getModelName())
                .size("1024x1024")
                .quality("medium")
                .outputFormat("png")
                .timeout(Duration.ofMinutes(2))
                .maxRetries(2)
                .logRequests(false)
                .logResponses(false)
                .build();
    }
}
