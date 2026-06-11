package com.aicommerce.auth.domain.handler.sms;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.aicommerce.auth.domain.command.AdminSmsPwsLoginCommand;
import com.aicommerce.auth.domain.mapper.AuthAdminMapper;
import com.aicommerce.auth.domain.model.Admin;
import com.aicommerce.auth.infra.SmsCodeCache;
import com.aicommerce.auth.utils.PasswordService;
import com.aicommerce.common.exception.BusinessException;
import com.aicommerce.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: AdminSmsLoginHandler
 * 描述: mng 验证码密码登录
 * 作者: xuzeyu
 * 创建时间: 2026/1/1
 */

@Component
public class AdminSmsPwsLoginHandler {

    @Resource
    private AuthAdminMapper authAdminMapper;

    @Resource
    private PasswordService passwordService;


    @Resource
    private SmsCodeCache smsCodeCache;

    public String handler(AdminSmsPwsLoginCommand command) {

        try {
            LambdaQueryWrapper<Admin> lambdaQuery = new LambdaQueryWrapper<>();
            lambdaQuery.eq(Admin::getUsername, command.getUsername());
            Admin admin = authAdminMapper.selectOne(lambdaQuery);
            if (admin == null) {
                throw new BusinessException("未查询到此管理员");
            }

            // 密码校验
            Boolean passwordValid = passwordService.passwordValid(command.getPassword(), admin.getPassword());
            if (!passwordValid) {
                throw new BusinessException("账户或密码错误");
            }

            // 验证码校验
            String expected = smsCodeCache.getCache(admin.getPhone()).orElse(null);
            if (expected == null || !StringUtils.equals(expected, command.getSmsCode())) {
                throw new BusinessException("验证码错误或已过期");
            }
            // 验证后删除
            smsCodeCache.removeCache(admin.getPhone());

            // 使用独立的 Admin StpLogic
            StpUtil.login(admin.getId());
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            return tokenInfo.getTokenValue() ;

        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }
}
