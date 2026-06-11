package com.jinHan.shop.core.admin.domain.handler;

import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinHan.shop.core.admin.domain.command.AdminPermissionCreateCommand;
import com.jinHan.shop.core.admin.domain.command.AdminPermissionUpdateCommand;
import com.jinHan.shop.core.admin.domain.mapper.AdminPermissionMapper;
import com.jinHan.shop.core.admin.domain.model.AdminPermission;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 类名: AdminPermissionHandler
 * 描述: 权限管理处理器
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Component
public class AdminPermissionHandler {

    @Resource
    private AdminPermissionMapper adminPermissionMapper;

    /**
     * 创建权限
     */
    public void create(AdminPermissionCreateCommand command) {
        Long count = adminPermissionMapper.selectCount(
                new LambdaQueryWrapper<AdminPermission>()
                        .eq(AdminPermission::getCode, command.getCode())
        );

        if (count > 0) {
            throw new BusinessException("权限编码已存在");
        }

        AdminPermission permission = new AdminPermission();
        permission.setName(command.getName());
        permission.setCode(command.getCode());

        int inserted = adminPermissionMapper.insert(permission);
        if (inserted <= 0) {
            throw new BusinessException("创建权限失败");
        }
    }

    /**
     * 更新权限
     */
    public void update(AdminPermissionUpdateCommand command) {
        AdminPermission existingPermission = adminPermissionMapper.selectById(command.getId());
        if (existingPermission == null) {
            throw new BusinessException("权限不存在");
        }

        if (command.getCode() != null && !command.getCode().equals(existingPermission.getCode())) {
            Long count = adminPermissionMapper.selectCount(
                    new LambdaQueryWrapper<AdminPermission>()
                            .eq(AdminPermission::getCode, command.getCode())
            );

            if (count > 0) {
                throw new BusinessException("权限编码已存在");
            }
            existingPermission.setCode(command.getCode());
        }

        if (command.getName() != null) {
            existingPermission.setName(command.getName());
        }

        int updated = adminPermissionMapper.updateById(existingPermission);
        if (updated <= 0) {
            throw new BusinessException("更新权限失败");
        }
    }

    /**
     * 删除权限
     */
    public void delete(Long id) {
        AdminPermission permission = adminPermissionMapper.selectById(id);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }

        int deleted = adminPermissionMapper.deleteById(id);
        if (deleted <= 0) {
            throw new BusinessException("删除权限失败");
        }
    }

    /**
     * 根据ID查询权限
     */
    public AdminPermission getById(Long id) {
        return adminPermissionMapper.selectById(id);
    }

    /**
     * 分页查询权限列表
     */
    public IPage<AdminPermission> queryPage(Integer page, Integer size) {
        Page<AdminPermission> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<AdminPermission> wrapper = new LambdaQueryWrapper<AdminPermission>()
                .orderByDesc(AdminPermission::getId);

        return adminPermissionMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 查询所有权限
     */
    public List<AdminPermission> queryAll() {
        return adminPermissionMapper.selectList(
                new LambdaQueryWrapper<AdminPermission>()
                        .orderByDesc(AdminPermission::getId)
        );
    }
}
