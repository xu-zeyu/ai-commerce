package com.jinHan.shop.admin.controller.admin.request;

import com.jinHan.shop.core.admin.domain.command.AdminRoleAssignPermissionCommand;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 类名: AdminRoleAssignPermissionRequest
 * 描述: 角色分配权限请求
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Data
public class AdminRoleAssignPermissionRequest {
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @NotEmpty(message = "权限列表不能为空")
    private List<Long> permissionIds;

    public AdminRoleAssignPermissionCommand toCommand() {
        AdminRoleAssignPermissionCommand command = new AdminRoleAssignPermissionCommand();
        command.setRoleId(this.roleId);
        command.setPermissionIds(this.permissionIds);
        return command;
    }
}
