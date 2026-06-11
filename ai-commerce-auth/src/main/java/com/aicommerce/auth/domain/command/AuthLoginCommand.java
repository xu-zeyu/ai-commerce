package com.aicommerce.auth.domain.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: AuthLoginCommand
 * 描述:  自定义登录命令
 * 作者: xuzeyu
 * 创建时间: 2025/12/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthLoginCommand {
    private String username;
    private String password;
}
