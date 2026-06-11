package com.jinHan.shop.admin.controller.admin.request;

import com.jinHan.shop.core.admin.domain.command.AdminRoleDeleteCommand;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 类名: AdminRoleDeleteRequest
 * 描述: 删除角色请求
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Data
public class AdminRoleDeleteRequest {
    @NotNull(message = "角色ID不能为空")
    private Long id;

    public AdminRoleDeleteCommand toCommand() {
        AdminRoleDeleteCommand command = new AdminRoleDeleteCommand();
        command.setId(this.id);
        return command;
    }
}
