package com.aicommerce.common.idempotent.keyresolver;

import com.aicommerce.common.idempotent.Idempotent;
import org.aspectj.lang.JoinPoint;

/**
 * @author Mao026
 * @date 2024/7/26 17:35
 */
public interface IdempotentKeyResolver {
    /**
     * 解析一个 Key
     *
     * @param idempotent 幂等注解
     * @param joinPoint  AOP 切面
     * @return Key
     */
    String resolver(JoinPoint joinPoint, Idempotent idempotent);
}
