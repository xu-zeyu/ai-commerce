package com.aicommerce.auth.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 类名: Users
 * 描述: 用户实体类
 * 作者: xuzeyu
 * 创建时间: 2026/1/15
 */
@Data
@TableName(value = "users")
public class Users {
    /**
     * 用户ID，主键
     */
    @TableId(type = IdType.AUTO)
    private Long userId;


    /**
     * 手机号，唯一
     */
    private String phone;

    /**
     * 密码
     */
    private String password;

    /**
     * 认证状态
     */
    private UsersStatusEnum status;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginAt;
}
