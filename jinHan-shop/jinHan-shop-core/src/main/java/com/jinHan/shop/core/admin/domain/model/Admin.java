package com.jinHan.shop.core.admin.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 类名: Admin
 * 描述: 管理员实体
 * 作者: xuzeyu
 * 创建时间: 2026/1/2
 */
@Data
@TableName(value = "admin")
public class Admin {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String realName;
    private String avatar;
    private LocalDateTime lastLoginTime;
    private Long roleId;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", phone='" + phone + '\'' +
                ", roleId=" + roleId +
                '}';
    }
}
