package com.aicommerce.starter.aiChat.service;


import com.aicommerce.starter.aiChat.model.ChatUserTypeEnum;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

//  ModelFactory 服务
public interface ChatService {

    /**
     * 聊天窗口
     */
    void chat(
            ChatUserTypeEnum userType,
            Long userId,
            Long modelId,
            String sessionId,
            String message,
            SseEmitter emitter);

}
