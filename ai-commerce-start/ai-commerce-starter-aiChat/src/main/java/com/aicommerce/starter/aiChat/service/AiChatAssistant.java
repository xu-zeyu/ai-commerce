package com.aicommerce.starter.aiChat.service;

import dev.langchain4j.service.TokenStream;

/**
 * LangChain4j 动态 AI 服务，用于流式回复及工具调用。
 */
public interface AiChatAssistant {

    TokenStream chat(String message);
}
