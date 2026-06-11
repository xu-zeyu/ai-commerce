package com.jinHan.shop.core.admin.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 类名: AdminRole
 * 描述: 管理员角色实体
 * 作者: xuzeyu
 * 创建时间: 2026/1/4
 */
@TableName(value = "admin_role")
@Data
public class AdminRole {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String rname;

    private String description;

    private LocalDateTime updatedTime;

    private LocalDateTime createdTime;

    private int deleted;
}
