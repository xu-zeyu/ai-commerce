package com.jinHan.shop.core.admin.domain.command;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 类名: AdminRoleCreateCommand
 * 描述: 创建管理员角色命令
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Data
public class AdminRoleCreateCommand {
    @NotBlank(message = "角色名称不能为空")
    private String rname;

    private String description;
}
