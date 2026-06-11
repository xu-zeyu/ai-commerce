package com.aicommerce.auth.domain.command;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: UserSmsPwsLoginCommand
 * 描述: 客户端验证码登录命令
 * 作者: xuzeyu
 * 创建时间: 2026/1/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSmsPwsLoginCommand {
    @NotNull(message = "手机号码不能为空")
    private String phone;
    @NotNull(message = "密码不能为空")
    private String password;
    @NotNull(message = "验证码不能为空")
    private String smsCode;
}
