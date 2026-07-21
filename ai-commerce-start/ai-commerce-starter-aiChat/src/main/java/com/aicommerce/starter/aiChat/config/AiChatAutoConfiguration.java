package com.aicommerce.starter.aiChat.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 类名: AiChatAutoConfiguration
 * 描述: AI 聊天自动配置
 * 作者: xuzeyu
 * 创建时间: 2026/7/21
 */
@Configuration
@MapperScan(basePackages = "com.aicommerce.starter.aiChat.mapper")
public class AiChatAutoConfiguration {
}
