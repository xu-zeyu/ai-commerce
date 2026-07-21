package com.aicommerce.starter.aiChat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

/**
 * 类名: ChatResponse
 * 描述:
 * 作者: xuzeyu
 * 创建时间: 2026/7/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {

    private Flux<String> content;
}
