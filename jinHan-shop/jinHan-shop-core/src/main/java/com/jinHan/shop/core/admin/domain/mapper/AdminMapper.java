package com.jinHan.shop.core.admin.domain.mapper;

import com.aicommerce.starter.mybatis.mapper.BaseMapperX;
import com.jinHan.shop.core.admin.domain.model.Admin;
import com.jinHan.shop.core.admin.domain.model.AdminPermission;
import com.jinHan.shop.core.admin.domain.model.AdminRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类名: AdminMapper
 * 描述: 管理员数据访问层
 * 作者: xuzeyu
 * 创建时间: 2026/1/2
 */
@Mapper
public interface AdminMapper  extends BaseMapperX<Admin> {
    List<AdminPermission> selectByRole(Object roleId);
    AdminRole selectByRoleName(Long roleId);
    List<Long> selectAdminIdsByPermissionCode(@Param("permissionCode") String permissionCode);

    /**
     * 删除角色的所有权限
     */
    void deleteRolePermissions(@Param("roleId") Long roleId);

    /**
     * 批量插入角色权限关联
     */
    void batchInsertRolePermissions(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);
}