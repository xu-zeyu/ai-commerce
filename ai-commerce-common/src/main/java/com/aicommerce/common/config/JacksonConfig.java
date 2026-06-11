package com.aicommerce.common.config;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.TimeZone;

import com.aicommerce.common.constant.Times;
import com.aicommerce.common.exception.BusinessException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new ParameterNamesModule())
            .addModule(new Jdk8Module()) // 处理 Optional 和其他 Java 8 特性
            .build();

        // 设置默认时区
        objectMapper.setTimeZone(TimeZone.getTimeZone(Times.GLOBAL_ZONE_ID));

        // 配置序列化特性
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 注册JavaTimeModule
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // 配置Instant的序列化
        javaTimeModule.addSerializer(Instant.class, new JsonSerializer<>() {
            @Override
            public void serialize(Instant instant, JsonGenerator gen, SerializerProvider provider) throws IOException {
                if (instant != null) {
                    gen.writeString(Times.DATE_TIME_FORMATTER.format(instant));
                }
            }
        });

        // 配置Instant的反序列化
        javaTimeModule.addDeserializer(Instant.class, new JsonDeserializer<>() {
            @Override
            public Instant deserialize(JsonParser p, DeserializationContext context) throws IOException {
                String dateString = p.getText();
                if (StringUtils.isEmpty(dateString)) {
                    return null;
                }
                try {
                    // 处理ISO 8601格式的时间字符串
                    if (dateString.contains("T")) {
                        Instant instant = Instant.parse(dateString);
                        // 将UTC时间转换为上海时间
                        return instant.atZone(ZoneId.of("UTC")).withZoneSameInstant(Times.GLOBAL_ZONE_ID).toInstant();
                    }
                    // 处理普通格式的时间字符串
                    return LocalDateTime.parse(dateString, Times.DATE_TIME_FORMATTER)
                        .atZone(Times.GLOBAL_ZONE_ID)
                        .toInstant();
                }
                catch (DateTimeParseException ex) {
                    throw new BusinessException("时间格式错误：" + dateString);
                }
            }
        });

        // 配置LocalDateTime的序列化
        javaTimeModule.addSerializer(LocalDateTime.class, new JsonSerializer<>() {
            @Override
            public void serialize(LocalDateTime localDateTime, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                if (localDateTime != null) {
                    gen.writeString(Times.DATE_TIME_FORMATTER.format(localDateTime));
                }
            }
        });

        // 配置LocalDateTime的反序列化
        javaTimeModule.addDeserializer(LocalDateTime.class, new JsonDeserializer<>() {
            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext context) throws IOException {
                String dateString = p.getText();
                if (StringUtils.isEmpty(dateString)) {
                    return null;
                }
                try {
                    // 如果字符串包含T，则认为是ISO 8601格式的时间字符串
                    if (dateString.contains("T")) {
                        return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);
                    }
                }
                catch (DateTimeParseException ex) {
                    throw new BusinessException("时间格式错误：" + dateString);
                }
                try {
                    return LocalDateTime.parse(dateString, Times.DATE_TIME_FORMATTER);
                }
                catch (DateTimeParseException ex) {
                    throw new BusinessException("时间格式错误：" + dateString);
                }
            }
        });

        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }

}