package com.jinHan.shop.admin.config;

import cn.dev33.satoken.fun.SaParamFunction;
import cn.dev33.satoken.interceptor.SaInterceptor;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Sa-Token Servlet 分发拦截器。
 *
 * <p>异步请求在初次 REQUEST 分发时已经完成鉴权。SSE 完成或发生异常后，
 * Spring MVC 会再次执行 ASYNC 或 ERROR 分发，此时不应重复读取 Sa-Token 上下文。</p>
 */
public class SaTokenDispatchInterceptor extends SaInterceptor {

    public SaTokenDispatchInterceptor(SaParamFunction<Object> auth) {
        super(auth);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        DispatcherType dispatcherType = request.getDispatcherType();
        if (dispatcherType == DispatcherType.ASYNC || dispatcherType == DispatcherType.ERROR) {
            return true;
        }
        return super.preHandle(request, response, handler);
    }
}
