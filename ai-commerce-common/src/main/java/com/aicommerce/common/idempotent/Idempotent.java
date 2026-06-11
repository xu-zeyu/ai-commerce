package com.aicommerce.common.idempotent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import com.aicommerce.common.idempotent.keyresolver.IdempotentKeyResolver;
import com.aicommerce.common.idempotent.keyresolver.impl.ExpressionIdempotentKeyResolver;


/**
 * 方法幂等控制注解。
 *
 * 通过 AOP 拦截标记了本注解的方法，根据 {@link #keyResolver()} 与 {@link #keyArg()} 解析出幂等 Key，
 * 在指定的 {@link #timeout()} 时间窗口内保证同一个 Key 只会成功执行一次。
 *
 * 推荐在控制器或应用层服务的方法上使用。
 *
 * @author Mao026
 * @author Howryann
 * @date 2024/7/26
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    /**
     * 幂等的超时时间, 默认为1s
     * @return
     */
    int timeout() default 1;

    /**
     * 时间单位 默认为秒
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * Key 解析器，默认使用基于 Spring EL 的实现。
     */
    Class<? extends IdempotentKeyResolver> keyResolver() default ExpressionIdempotentKeyResolver.class;

    /**
     * Key 解析表达式，基于 Spring EL。
     * <p>
     * 默认表达式： {@code "#class + #name + #id"}<br/>
     * 常用变量： {@code #id/#userId} 当前用户、{@code #tenantId} 当前租户、
     * {@code #class/#className} 类名、{@code #name/#methodName} 方法名，以及方法参数名或 {@code #p0/#p1...}。
     */
    String keyArg() default "#class + #name + #id";

    /**
     * 方法执行抛出异常时，是否立即删除幂等 Key。
     * <p>
     * 默认开启，避免失败后短时间内无法重试。
     */
    boolean deleteKeyWhenException() default true;
}
