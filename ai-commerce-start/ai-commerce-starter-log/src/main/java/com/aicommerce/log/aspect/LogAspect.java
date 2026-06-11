package com.aicommerce.log.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.aicommerce.auth.domain.model.Admin;
import com.aicommerce.log.annotation.Log;
import com.aicommerce.log.entity.LogEntity;
import com.aicommerce.log.mapper.LogAdminMapper;
import com.aicommerce.log.service.LogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志切面
 */
@Aspect
@Component
public class LogAspect {

    @Resource
    private LogService logService;

    @Resource
    private LogAdminMapper logAdminMapper;

    @Around(value = "@annotation(com.aicommerce.log.annotation.Log)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 开始时间
        long startTime = System.currentTimeMillis();
        // 执行结果
        Object result = null;
        // 异常信息
        String errorMessage = null;
        // 是否成功
        boolean success = true;

        try {
            // 执行目标方法
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            errorMessage = throwable.getMessage();
            success = false;
            throw throwable;
        } finally {
            // 计算执行时间
            long executeTime = System.currentTimeMillis() - startTime;
            // 记录日志
            recordLog(joinPoint, result, errorMessage, success, executeTime);
        }

        return result;
    }

    private void recordLog(ProceedingJoinPoint joinPoint, Object result, String errorMessage, boolean success, long executeTime) {
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取Log注解
        Log logAnnotation = method.getAnnotation(Log.class);

        // 构建日志实体
        LogEntity logEntity = new LogEntity()
                .setOperator(getCurrentUser())
                .setOperationType(logAnnotation.operationType())
                .setDescription(logAnnotation.value())
                .setRequestUrl(request.getRequestURL().toString())
                .setRequestMethod(request.getMethod())
                .setIp(request.getRemoteAddr())
                .setOperateTime(LocalDateTime.now())
                .setExecuteTime(executeTime)
                .setSuccess(success)
                .setErrorMessage(errorMessage);

        // 记录请求参数
        if (logAnnotation.recordParams()) {
            logEntity.setRequestParams(getRequestParams(request, joinPoint));
        }

        // 记录响应结果
        if (logAnnotation.recordResult()) {
            logEntity.setResponseResult(result != null ? result.toString() : null);
        }

        // 保存日志
        logService.saveLog(logEntity);
        // 同时打印日志，方便调试
        System.out.println("Log: " + logEntity);
    }

    private String getCurrentUser() {
        try {
            if (StpUtil.isLogin()) {
                Admin admin = logAdminMapper.selectById(StpUtil.getLoginId().toString());
                return admin.getUsername();
            }
        } catch (Exception e) {
            // 未登录或获取用户信息失败
        }
        return "匿名用户";
    }

    private String getRequestParams(jakarta.servlet.http.HttpServletRequest request, ProceedingJoinPoint joinPoint) {
        // 获取请求参数
        Map<String, Object> params = new HashMap<>();
        // 获取URL参数
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            params.put(name, request.getParameter(name));
        }
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            String[] parameterNamesArray = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
            for (int i = 0; i < args.length; i++) {
                if (parameterNamesArray != null && i < parameterNamesArray.length) {
                    params.put(parameterNamesArray[i], args[i]);
                }
            }
        }
        return params.toString();
    }
}
