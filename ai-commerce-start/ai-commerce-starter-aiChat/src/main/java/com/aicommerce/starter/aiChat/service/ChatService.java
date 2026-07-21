package com.aicommerce.starter.aiChat.service;


import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

//  ModelFactory 服务
public interface ChatService {

    /**
     * 聊天方法
     */
    void chat(Long modelId, String message, SseEmitter emitter);
}
