package com.aicommerce.common.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;

/**
 * JSON 工具类
 */
public final class JsonUtils {

    /**
     * 由 Spring 容器注入的 ObjectMapper，可被外部初始化。 注意：这里不能是 final 的，因为它需要被外部设置。
     */
    private static ObjectMapper springManagedMapper;

    /**
     * 提供一个静态方法，用于让 Spring 初始化器注入 Bean。
     * @param objectMapper Spring 容器管理的 ObjectMapper 实例
     */
    public static void init(ObjectMapper objectMapper) {
        JsonUtils.springManagedMapper = objectMapper;
    }

    private JsonUtils() {
    }

    /**
     * 获取 ObjectMapper 实例。
     * <p>
     * 优先返回由 Spring 管理的实例，如果不存在，则返回一个内部维护的默认实例。
     * @return objectMapper 实例
     */
    public static ObjectMapper getObjectMapper() {
        return Objects.requireNonNullElse(springManagedMapper, DefaultObjectMapperHolder.INSTANCE);
    }

    /**
     * 将对象转换为JSON格式的字符串
     * @param object 要转换的对象
     * @return JSON格式的字符串，如果对象为null，则返回null
     * @throws RuntimeException 如果转换过程中发生JSON处理异常，则抛出运行时异常
     */
    public static String toJsonString(Object object) {
        if (object == null) {
            return "";
        }
        try {
            return getObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 将JSON格式的字符串转换为指定类型的对象
     * @param text JSON格式的字符串
     * @param clazz 要转换的目标对象类型
     * @param <T> 目标对象的泛型类型
     * @return 转换后的对象，如果字符串为空则返回空对象
     * @throws RuntimeException 如果转换过程中发生IO异常，则抛出运行时异常
     */
    public static <T> T parseObject(String text, Class<T> clazz) {
        String json = text;
        if (StringUtils.isBlank(json)) {
            json = "{}";
        }
        try {
            return getObjectMapper().readValue(json, clazz);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 将字节数组转换为指定类型的对象
     * @param bytes 字节数组
     * @param clazz 要转换的目标对象类型
     * @param <T> 目标对象的泛型类型
     * @return 转换后的对象，如果字节数组为空则返回null
     * @throws RuntimeException 如果转换过程中发生IO异常，则抛出运行时异常
     */
    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        byte[] jsonBytes = bytes;
        if (ArrayUtils.isEmpty(jsonBytes)) {
            jsonBytes = new byte[0];
        }
        try {
            return getObjectMapper().readValue(jsonBytes, clazz);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 将JSON格式的字符串转换为指定类型的对象，支持复杂类型
     * @param text JSON格式的字符串
     * @param typeReference 指定类型的TypeReference对象
     * @param <T> 目标对象的泛型类型
     * @return 转换后的对象，如果字符串为空则返回null
     * @throws RuntimeException 如果转换过程中发生IO异常，则抛出运行时异常
     */
    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        String json = text;
        if (StringUtils.isBlank(json)) {
            json = "{}";
        }
        try {
            return getObjectMapper().readValue(json, typeReference);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 将JSON格式的字符串转换为指定类型对象的列表
     * @param text JSON格式的字符串
     * @param clazz 要转换的目标对象类型
     * @param <T> 目标对象的泛型类型
     * @return 转换后的对象的列表，如果字符串为空则返回空列表
     * @throws RuntimeException 如果转换过程中发生IO异常，则抛出运行时异常
     */
    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StringUtils.isEmpty(text)) {
            return new ArrayList<>();
        }
        try {
            return getObjectMapper().readValue(text,
                    getObjectMapper().getTypeFactory().constructCollectionType(List.class, clazz));
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static JsonNode parseJsonNode(String text) {
        String json = text;
        if (StringUtils.isBlank(json)) {
            json = "{}";
        }
        try {
            return getObjectMapper().readTree(json);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 使用静态内部类 + final 实例的方式实现懒加载的单例默认 ObjectMapper。
     */
    private static class DefaultObjectMapperHolder {

        // 这里可以对默认的 ObjectMapper 进行自定义配置
        private static final ObjectMapper INSTANCE = new ObjectMapper();

        static {
            // 配置默认实例
            // INSTANCE.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        }

    }

}