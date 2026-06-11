package com.aicommerce.common.log;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.slf4j.MDC;

/**
 * 应用追踪 id traceId 工具类，基于 MDC 实现
 */
public final class ApplicationTraceIdHelper {

    public static final String TRACE_ID_MDC_KEY = "traceId";

    private ApplicationTraceIdHelper() {
    }

    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase(Locale.ENGLISH);
    }

    /**
     * 设置请求唯一 ID
     */
    public static void setTraceIdIfAbsent() {
        if (MDC.get(TRACE_ID_MDC_KEY) == null) {
            MDC.put(TRACE_ID_MDC_KEY, generate());
        }
    }

    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID_MDC_KEY, traceId);
    }

    public static void removeTraceId() {
        MDC.remove(TRACE_ID_MDC_KEY);
    }

    /**
     * 从 mdc 中获取 traceId
     * @return 返回 mdc 中的 traceId, 没有时返回空字符串
     */
    public static String getTraceId() {
        return Optional.ofNullable(MDC.get(TRACE_ID_MDC_KEY)).orElse("");
    }

    public static <T> Callable<T> wrap(final Callable<T> callable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            }
            else {
                MDC.setContextMap(context);
            }
            setTraceIdIfAbsent();
            try {
                return callable.call();
            }
            finally {
                MDC.clear();
            }
        };
    }

    public static Runnable wrap(final Runnable runnable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            }
            else {
                MDC.setContextMap(context);
            }
            // 设置 traceId
            setTraceIdIfAbsent();
            try {
                runnable.run();
            }
            finally {
                MDC.clear();
            }
        };
    }

}