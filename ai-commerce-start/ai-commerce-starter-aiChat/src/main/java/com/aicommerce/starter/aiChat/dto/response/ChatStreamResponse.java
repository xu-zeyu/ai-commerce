package com.aicommerce.starter.aiChat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI 聊天流式事件响应。
 *
 * @author xuzeyu
 */
@Getter
@AllArgsConstructor
public class ChatStreamResponse {

    private final String content;
}
