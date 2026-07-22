package com.jinHan.shop.admin.controller.aiChat.response;

import com.aicommerce.starter.aiChat.entity.AiChatMemoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 类名: AiMemoryResponse
 * 描述:
 * 作者: xuzeyu
 * 创建时间: 2026/7/22
 */
@Data
@AllArgsConstructor
public class AiMemoryResponse {

    private Long id;

    private Long modelId;

    private String sessionId;

    private String messagesJson;

    public static AiMemoryResponse from(AiChatMemoryEntity model) {
        return new AiMemoryResponse(model.getId(), model.getModelId(), model.getSessionId(),model.getMessagesJson());
    }
}
