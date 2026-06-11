package com.jinHan.shop.core.admin.domain.command;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 类名: AdminRoleAssignPermissionCommand
 * 描述: 角色分配权限命令
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Data
public class AdminRoleAssignPermissionCommand {
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @NotEmpty(message = "权限列表不能为空")
    private List<Long> permissionIds;
}
