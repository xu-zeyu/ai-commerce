package com.aicommerce.starter.aiChat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
     * 用户消息
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;
}
