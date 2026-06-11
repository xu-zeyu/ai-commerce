package com.jinHan.shop.admin.controller.admin.response;

import com.jinHan.shop.core.admin.domain.model.Admin;
import com.jinHan.shop.core.admin.domain.model.AdminRole;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 类名: AdminInfoResponse
 * 描述: 管理员信息返回数据
 * 作者: xuzeyu
 * 创建时间: 2026/1/4
 */
@Data
@NoArgsConstructor
public class AdminInfoResponse {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String avatar;
    private LocalDateTime createdTime;
    private LocalDateTime lastLoginTime;
    private LocalDateTime updatedTime;
    private AdminRole role;

    public AdminInfoResponse(Admin admin, AdminRole adminRole) {
        id = admin.getId();
        username = admin.getUsername();
        realName = admin.getRealName();
        phone = admin.getPhone();
        avatar = admin.getAvatar();
        createdTime = admin.getCreatedTime();
        lastLoginTime = admin.getLastLoginTime();
        updatedTime = admin.getUpdatedTime();
        role = adminRole;
    }
}
