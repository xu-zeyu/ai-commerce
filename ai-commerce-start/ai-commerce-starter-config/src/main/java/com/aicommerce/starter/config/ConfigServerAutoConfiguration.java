package com.aicommerce.starter.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Configuration;

/**
 * 配置中心自动配置类
 */
@Configuration
@EnableConfigServer
@ConditionalOnProperty(name = "spring.cloud.config.server.enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnMissingBean(ConfigServerAutoConfiguration.class)
public class ConfigServerAutoConfiguration {
}
