package com.jinHan.shop.admin.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
            // 登录校验。排除开放接口与 Spring 错误分发，避免 404/异常页面再次进入鉴权链。
            SaRouter
                    .match("/**")
                    .notMatch("/login/sms", "/public/**", "/error")
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}
