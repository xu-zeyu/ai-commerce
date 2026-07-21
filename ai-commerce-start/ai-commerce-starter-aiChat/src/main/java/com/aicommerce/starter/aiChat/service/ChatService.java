package com.aicommerce.starter.aiChat.service;


//  ModelFactory 服务
public interface ChatService {

    /**
     * 保存日志
     */
     String chat(Long modelId,String message);
}
