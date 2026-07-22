package com.aicommerce.starter.aiChat.service;


import dev.langchain4j.service.SystemMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

//  ModelFactory 服务
public interface ChatService {

    /**
     * 聊天窗口
     */
    @SystemMessage
    void chat(Long modelId, String message, SseEmitter emitter);

    /*
    * 聊天记忆
    *
    * */

//    void chatMemory();

}
