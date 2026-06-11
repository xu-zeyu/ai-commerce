package com.aicommerce.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * JsonUtils 的 Spring 初始化器。
 * <p>
 * 这个组件的唯一作用是在 Spring 容器启动后， 将容器中配置的 ObjectMapper Bean 注入到 JsonUtils 工具类中。
 */
@Component
public class JsonUtilsInitializer {

    @Resource
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        JsonUtils.init(this.objectMapper);
    }

}