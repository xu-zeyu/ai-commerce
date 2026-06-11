package com.aicommerce.common.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Howryann
 * @date 2025/11/12
 **/
@Configuration
public class DefaultCacheConfiguration {

    @Bean
    @ConditionalOnMissingBean(Cache.class)
    public Cache cache() {
        return new DefaultCache();
    }

}
