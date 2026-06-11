package com.jinHan.shop.core.admin.domain.mapper;

import com.aicommerce.starter.mybatis.mapper.BaseMapperX;
import com.jinHan.shop.core.admin.domain.model.AdminPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 类名: AdminPermissionMapper
 * 描述: 管理员权限数据访问层
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Mapper
public interface AdminPermissionMapper extends BaseMapperX<AdminPermission> {
}
