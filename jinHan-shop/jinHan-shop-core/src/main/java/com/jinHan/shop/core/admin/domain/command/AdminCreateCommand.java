package com.jinHan.shop.core.admin.domain.command;

import lombok.Data;

/**
 * 类名: AdminCreateCommand
 * 描述: 创建管理员命令
 * 作者: xuzeyu
 * 创建时间: 2026/6/15
 */
@Data
public class AdminCreateCommand {
    private String username;
    private String password;
    private String phone;
    private String realName;
    private Long roleId;
}
