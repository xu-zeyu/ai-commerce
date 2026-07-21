package com.aicommerce.starter.aiChat.dto.request;
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
    private Long modelId;

    /**
     * 用户消息
     */
    private String message;
}
