package com.jinHan.shop.admin.controller.admin.request;

import com.jinHan.shop.core.admin.domain.command.AdminRoleUpdateCommand;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 类名: AdminRoleUpdateRequest
 * 描述: 更新角色请求
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Data
public class AdminRoleUpdateRequest {
    @NotNull(message = "角色ID不能为空")
    private Long id;

    private String rname;

    private String description;

    public AdminRoleUpdateCommand toCommand() {
        AdminRoleUpdateCommand command = new AdminRoleUpdateCommand();
        command.setId(this.id);
        command.setRname(this.rname);
        command.setDescription(this.description);
        return command;
    }
}
