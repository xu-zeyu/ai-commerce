package com.jinHan.shop.core.admin.domain.command;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 类名: AdminPermissionUpdateCommand
 * 描述: 更新权限命令
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Data
public class AdminPermissionUpdateCommand {
    @NotNull(message = "权限ID不能为空")
    private Long id;

    private String name;

    private String code;
}
