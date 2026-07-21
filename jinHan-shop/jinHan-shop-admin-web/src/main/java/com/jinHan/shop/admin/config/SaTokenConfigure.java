package com.jinHan.shop.admin.config;

import cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 类名: SaTokenConfigure [Sa-Token 权限认证] 配置类
 * 描述: 注册全局过滤器，使用独立的 Admin StpLogic
 * 作者: xuzeyu
 * 创建时间: 2026/1/1
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    /**
     * 让 Sa-Token 上下文过滤器同时参与 Spring MVC 的异步分发。
     * SseEmitter 完成时会触发一次 ASYNC 分发，如果未重新初始化上下文，
     * SaInterceptor 会抛出“SaTokenContext 上下文尚未初始化”。
     */
    @Bean
    public FilterRegistrationBean<SaTokenContextFilterForJakartaServlet> saTokenContextFilterRegistration(
            SaTokenContextFilterForJakartaServlet filter) {
        FilterRegistrationBean<SaTokenContextFilterForJakartaServlet> registration =
                new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setAsyncSupported(true);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);
        return registration;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
            // 登录校验。排除开放接口与 Spring 错误分发，避免 404/异常页面再次进入鉴权链。
            SaRouter
                    .match("/**")
                    .notMatch("/login/sms", "/public/**", "/error", "/v3/api-docs", "/v3/api-docs/**")
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}
