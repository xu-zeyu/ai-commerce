package com.jinHan.shop.core.admin.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 类名: AdminPermission
 * 描述: 管理员权限实体
 * 作者: xuzeyu
 * 创建时间: 2026/1/2
 */
@TableName(value = "admin_permission")
@Data
public class AdminPermission {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;
}
