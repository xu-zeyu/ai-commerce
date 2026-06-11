package com.aicommerce.common.idempotent;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.aicommerce.common.cache.Cache;
import com.aicommerce.common.exception.BusinessException;
import com.aicommerce.common.idempotent.keyresolver.IdempotentKeyResolver;
import com.aicommerce.common.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 幂等切面
 *
 * 通过 {@link Idempotent} 注解 + 可插拔的 {@link IdempotentKeyResolver} 实现幂等控制，
 * 底层依赖 {@link Cache} 抽象，默认使用内存实现，可通过缓存 Starter 切换为分布式实现。
 *
 * @author Mao026
 * @author Howryann
 * @date 2024/7/26
 */
@Aspect
@Component
@Slf4j
public class IdempotentAspect {

    /**
     * 缓存 key 模板
     */
    private static final String IDEMPOTENT_KEY_PATTERN = "idempotent:%s";

    private final Map<Class<? extends IdempotentKeyResolver>, IdempotentKeyResolver> keyResolvers;

    private final Cache cache;

    public IdempotentAspect(List<IdempotentKeyResolver> keyResolvers, Cache cache) {
        this.keyResolvers = CollectionUtils.convertMap(keyResolvers, IdempotentKeyResolver::getClass);
        this.cache = cache;
    }

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        IdempotentKeyResolver keyResolver = this.keyResolvers.get(idempotent.keyResolver());
        if (Objects.isNull(keyResolver)) {
            return joinPoint.proceed();
        }

        String businessKey = keyResolver.resolver(joinPoint, idempotent);
        if (StringUtils.isBlank(businessKey)) {
            // 未解析出 key，直接放行
            return joinPoint.proceed();
        }

        boolean acquired = tryAcquire(businessKey, idempotent.timeout(), idempotent.timeUnit());
        if (!acquired) {
            long seconds = getRemainingSeconds(businessKey);
            throw new BusinessException("请{}s后操作", seconds);
        }

        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            if (idempotent.deleteKeyWhenException()) {
                release(businessKey);
            }
            throw throwable;
        }
    }

    /**
     * 尝试占用幂等 Key
     */
    private boolean tryAcquire(String businessKey, long timeout, TimeUnit timeUnit) {
        String cacheKey = formatKey(businessKey);
        long now = Instant.now().toEpochMilli();
        long expireMillis = timeUnit.toMillis(timeout);
        long expireAt = now + expireMillis;

        Long existExpireAt = this.cache.get(cacheKey, Long.class);
        if (existExpireAt != null && existExpireAt > now) {
            // 已经存在且未过期
            return false;
        }

        // 设置新的过期时间
        try {
            this.cache.set(cacheKey, expireAt, Duration.ofMillis(expireMillis));
        } catch (UnsupportedOperationException ex) {
            // 兼容不支持过期时间的 Cache 实现
            this.cache.setCache(cacheKey, expireAt);
        }
        return true;
    }

    /**
     * 获取剩余秒数
     */
    private long getRemainingSeconds(String businessKey) {
        String cacheKey = formatKey(businessKey);
        Long expireAt = this.cache.get(cacheKey, Long.class);
        if (expireAt == null) {
            return 0L;
        }
        long now = Instant.now().toEpochMilli();
        long remainingMillis = expireAt - now;
        if (remainingMillis <= 0) {
            return 0L;
        }
        return TimeUnit.SECONDS.convert(remainingMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * 释放幂等 Key
     */
    private void release(String businessKey) {
        String cacheKey = formatKey(businessKey);
        this.cache.delete(cacheKey);
    }

    private static String formatKey(String key) {
        return String.format(IDEMPOTENT_KEY_PATTERN, key);
    }
}
