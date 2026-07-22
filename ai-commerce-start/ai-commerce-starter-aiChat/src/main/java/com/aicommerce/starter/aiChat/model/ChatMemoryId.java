package com.aicommerce.starter.aiChat.model;

import java.util.Objects;

/**
 * 用户在指定模型下的唯一聊天记忆标识。
 */
public record ChatMemoryId(ChatUserTypeEnum userType, Long userId, Long modelId, String sessionId) {

    public ChatMemoryId {
        Objects.requireNonNull(userType, "userType must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(modelId, "modelId must not be null");
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        if (sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId must not be blank");
        }
        if (sessionId.length() > 64) {
            throw new IllegalArgumentException("sessionId length must not exceed 64");
        }
    }
}
