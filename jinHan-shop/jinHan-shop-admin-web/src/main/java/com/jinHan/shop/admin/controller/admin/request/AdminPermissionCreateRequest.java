package com.jinHan.shop.admin.controller.admin.request;

import com.jinHan.shop.core.admin.domain.command.AdminPermissionCreateCommand;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 类名: AdminPermissionCreateRequest
 * 描述: 创建权限请求
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Data
public class AdminPermissionCreateRequest {
    @NotBlank(message = "权限名称不能为空")
    private String name;

    @NotBlank(message = "权限编码不能为空")
    private String code;

    public AdminPermissionCreateCommand toCommand() {
        AdminPermissionCreateCommand command = new AdminPermissionCreateCommand();
        command.setName(this.name);
        command.setCode(this.code);
        return command;
    }
}
