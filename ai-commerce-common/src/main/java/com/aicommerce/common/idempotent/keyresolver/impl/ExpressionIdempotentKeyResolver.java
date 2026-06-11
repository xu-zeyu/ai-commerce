package com.aicommerce.common.idempotent.keyresolver.impl;

import java.lang.reflect.Method;

import com.aicommerce.common.idempotent.Idempotent;
import com.aicommerce.common.idempotent.keyresolver.IdempotentKeyResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

/**
 * 基于 Spring EL 表达式的幂等 Key 解析器。
 *
 * 支持的上下文变量示例：
 * <ul>
 *     <li>{@code #id / #userId} 当前登录用户 ID</li>
 *     <li>{@code #tenantId} 当前租户 ID（可能为空）</li>
 *     <li>{@code #class} / {@code #className} 方法所在类名</li>
 *     <li>{@code #name} / {@code #methodName} 方法名</li>
 *     <li>方法参数：{@code #paramName} 或 {@code #p0, #p1, ...}</li>
 * </ul>
 *
 * 默认表达式见 {@link Idempotent#keyArg()}。
 *
 * @author Mao026
 * @author Howryann
 * @date 2024/7/26
 */
@Slf4j
@Service
public class ExpressionIdempotentKeyResolver implements IdempotentKeyResolver {

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private final ExpressionParser expressionParser = new SpelExpressionParser();

    @Override
    public String resolver(JoinPoint joinPoint, Idempotent idempotent) {
        Method method = getMethod(joinPoint);

        StandardEvaluationContext context = new StandardEvaluationContext();

        // todo 获取当前用户
        String userId = "userId";

        context.setVariable("id", userId);
        context.setVariable("userId", userId);

        context.setVariable("name", method.getName());
        context.setVariable("methodName", method.getName());
        context.setVariable("class", method.getDeclaringClass().getSimpleName());
        context.setVariable("className", method.getDeclaringClass().getSimpleName());

        // 方法参数：支持 #paramName 和 #p0 方式
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = this.parameterNameDiscoverer.getParameterNames(method);
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length && i < args.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }
        for (int i = 0; i < args.length; i++) {
            context.setVariable("p" + i, args[i]);
        }

        try {
            Expression expression = this.expressionParser.parseExpression(idempotent.keyArg());
            String value = expression.getValue(context, String.class);
            if (StringUtils.isBlank(value)) {
                return null;
            }
            return value;
        } catch (Exception ex) {
            log.error("解析幂等 Key 表达式失败, expression: {}", idempotent.keyArg(), ex);
            return null;
        }
    }

    private static Method getMethod(JoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        if (!method.getDeclaringClass().isInterface()) {
            return method;
        }

        try {
            return point.getTarget()
                    .getClass()
                    .getDeclaredMethod(point.getSignature().getName(), method.getParameterTypes());
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException("无法获取目标方法", ex);
        }
    }
}
