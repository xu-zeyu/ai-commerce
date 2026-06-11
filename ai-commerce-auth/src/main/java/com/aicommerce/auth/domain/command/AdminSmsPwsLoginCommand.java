package com.aicommerce.auth.domain.command;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: SmsLoginCommand
 * 描述: 验证码登录命令
 * 作者: xuzeyu
 * 创建时间: 2025/12/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminSmsPwsLoginCommand {
    @NotNull(message = "用户名不能为空")
    private String username;
    @NotNull(message = "密码不能为空")
    private String password;
    @NotNull(message = "验证码不能为空")
    private String smsCode;
}
