package com.jinHan.shop.admin.controller.admin.request;

import com.jinHan.shop.core.admin.domain.command.AdminPermissionUpdateCommand;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 类名: AdminPermissionUpdateRequest
 * 描述: 更新权限请求
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Data
public class AdminPermissionUpdateRequest {
    @NotNull(message = "权限ID不能为空")
    private Long id;

    private String name;

    private String code;

    public AdminPermissionUpdateCommand toCommand() {
        AdminPermissionUpdateCommand command = new AdminPermissionUpdateCommand();
        command.setId(this.id);
        command.setName(this.name);
        command.setCode(this.code);
        return command;
    }
}
