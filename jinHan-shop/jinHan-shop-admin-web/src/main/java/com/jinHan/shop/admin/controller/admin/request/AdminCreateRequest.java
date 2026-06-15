package com.jinHan.shop.admin.controller.admin.request;

import com.jinHan.shop.core.admin.domain.command.AdminCreateCommand;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 类名: AdminCreateRequest
 * 描述: 创建管理员请求
 * 作者: xuzeyu
 * 创建时间: 2026/6/15
 */
@Data
public class AdminCreateRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String phone;

    private String realName;

    private Long roleId;

    public AdminCreateCommand toCommand() {
        AdminCreateCommand command = new AdminCreateCommand();
        command.setUsername(this.username);
        command.setPassword(this.password);
        command.setPhone(this.phone);
        command.setRealName(this.realName);
        command.setRoleId(this.roleId);
        return command;
    }
}
