package com.aicommerce.starter.aiChat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: ChatRequest
 * 描述:
 * 作者: xuzeyu
 * 创建时间: 2026/7/21
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {

    /**
     * 数据库中的模型ID
     */
    @NotNull(message = "模型ID不能为空")
    private Long modelId;

    /**
     * 客户端生成的会话ID，用于隔离同一用户的不同聊天上下文
     */
    @NotBlank(message = "会话ID不能为空")
    @Size(max = 64, message = "会话ID长度不能超过64个字符")
    private String sessionId;

    /**
     * 用户消息
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;
}
