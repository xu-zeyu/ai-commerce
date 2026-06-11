package com.jinHan.shop.admin.controller.login;

import com.aicommerce.auth.domain.command.AdminSmsPwsLoginCommand;
import com.aicommerce.auth.domain.handler.sms.AdminSmsPwsLoginHandler;
import com.aicommerce.common.model.Result;
import com.aicommerce.log.annotation.Log;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类名: LoginController
 * 描述: 登录接口
 * 作者: xuzeyu
 * 创建时间: 2025/12/23
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Resource
    private AdminSmsPwsLoginHandler adminSmsPwsLoginHandler;

    @Log(value = "短信验证码登录", operationType = "LOGIN_SMS")
    @PostMapping("/sms")
    public Result<String> smsLogin(@RequestBody @Validated AdminSmsPwsLoginCommand command) {
        return Result.success(adminSmsPwsLoginHandler.handler(command));
    }
}
