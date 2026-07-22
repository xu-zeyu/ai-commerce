package com.jinHan.shop.admin.controller.aiChat.response;

import com.aicommerce.starter.aiChat.entity.AiModelEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 可用 AI 模型响应。
 */
@Data
@AllArgsConstructor
public class AiModelResponse {

    private Long id;

    private String provider;

    private String modelName;

    public static AiModelResponse from(AiModelEntity model) {
        return new AiModelResponse(model.getId(), model.getProvider(), model.getModelName());
    }
}
