package com.jinHan.shop.admin.controller.admin.request;

import com.jinHan.shop.core.admin.domain.command.AdminRoleCreateCommand;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 类名: AdminRoleCreateRequest
 * 描述: 创建角色请求
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Data
public class AdminRoleCreateRequest {
    @NotBlank(message = "角色名称不能为空")
    private String rname;

    private String description;

    public AdminRoleCreateCommand toCommand() {
        AdminRoleCreateCommand command = new AdminRoleCreateCommand();
        command.setRname(this.rname);
        command.setDescription(this.description);
        return command;
    }
}
