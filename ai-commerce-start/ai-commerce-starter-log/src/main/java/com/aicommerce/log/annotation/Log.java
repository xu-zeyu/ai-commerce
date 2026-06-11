package com.aicommerce.log.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 操作描述
     */
    String value() default "";

    /**
     * 操作类型
     */
    String operationType() default "";

    /**
     * 是否记录请求参数
     */
    boolean recordParams() default true;

    /**
     * 是否记录响应结果
     */
    boolean recordResult() default true;

    /**
     * 是否记录异常
     */
    boolean recordException() default true;
}
