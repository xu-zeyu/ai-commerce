package com.jinHan.shop.core.admin.domain.command;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 类名: AdminPermissionCreateCommand
 * 描述: 创建权限命令
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Data
public class AdminPermissionCreateCommand {
    @NotBlank(message = "权限名称不能为空")
    private String name;

    @NotBlank(message = "权限编码不能为空")
    private String code;
}
