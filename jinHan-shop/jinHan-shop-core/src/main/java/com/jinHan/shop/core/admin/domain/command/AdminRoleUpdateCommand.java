package com.jinHan.shop.core.admin.domain.command;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 类名: AdminRoleUpdateCommand
 * 描述: 更新管理员角色命令
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Data
public class AdminRoleUpdateCommand {
    @NotNull(message = "角色ID不能为空")
    private Long id;

    private String rname;

    private String description;
}
