package com.jinHan.shop.core.admin.domain.handler;

import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinHan.shop.core.admin.domain.command.AdminRoleAssignPermissionCommand;
import com.jinHan.shop.core.admin.domain.command.AdminRoleCreateCommand;
import com.jinHan.shop.core.admin.domain.command.AdminRoleDeleteCommand;
import com.jinHan.shop.core.admin.domain.command.AdminRoleUpdateCommand;
import com.jinHan.shop.core.admin.domain.mapper.AdminMapper;
import com.jinHan.shop.core.admin.domain.mapper.AdminPermissionMapper;
import com.jinHan.shop.core.admin.domain.mapper.AdminRoleMapper;
import com.jinHan.shop.core.admin.domain.model.AdminPermission;
import com.jinHan.shop.core.admin.domain.model.AdminRole;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 类名: AdminRoleHandler
 * 描述: 管理员角色业务处理器
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Component
public class AdminRoleHandler {

    @Resource
    private AdminRoleMapper adminRoleMapper;

    @Resource
    private AdminPermissionMapper adminPermissionMapper;

    @Resource
    private AdminMapper adminMapper;

    /**
     * 创建角色
     */
    public void create(AdminRoleCreateCommand command) {
        Long count = adminRoleMapper.selectCount(
                new LambdaQueryWrapper<AdminRole>()
                        .eq(AdminRole::getRname, command.getRname())
                        .eq(AdminRole::getDeleted, 0)
        );

        if (count > 0) {
            throw new BusinessException("角色名称已存在");
        }

        AdminRole role = new AdminRole();
        role.setRname(command.getRname());
        role.setDescription(command.getDescription());
        role.setCreatedTime(LocalDateTime.now());
        role.setUpdatedTime(LocalDateTime.now());
        role.setDeleted(0);

        int inserted = adminRoleMapper.insert(role);
        if (inserted <= 0) {
            throw new BusinessException("创建角色失败");
        }
    }

    /**
     * 更新角色
     */
    public void update(AdminRoleUpdateCommand command) {
        AdminRole existingRole = adminRoleMapper.selectById(command.getId());
        if (existingRole == null || existingRole.getDeleted() == 1) {
            throw new BusinessException("角色不存在");
        }

        if (command.getRname() != null && !command.getRname().equals(existingRole.getRname())) {
            Long count = adminRoleMapper.selectCount(
                    new LambdaQueryWrapper<AdminRole>()
                            .eq(AdminRole::getRname, command.getRname())
                            .eq(AdminRole::getDeleted, 0)
            );

            if (count > 0) {
                throw new BusinessException("角色名称已存在");
            }
            existingRole.setRname(command.getRname());
        }

        if (command.getDescription() != null) {
            existingRole.setDescription(command.getDescription());
        }

        existingRole.setUpdatedTime(LocalDateTime.now());

        int updated = adminRoleMapper.updateById(existingRole);
        if (updated <= 0) {
            throw new BusinessException("更新角色失败");
        }
    }

    /**
     * 删除角色
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(AdminRoleDeleteCommand command) {
        AdminRole role = adminRoleMapper.selectById(command.getId());
        if (role == null || role.getDeleted() == 1) {
            throw new BusinessException("角色不存在");
        }

        role.setDeleted(1);
        role.setUpdatedTime(LocalDateTime.now());

        int updated = adminRoleMapper.updateById(role);
        if (updated <= 0) {
            throw new BusinessException("删除角色失败");
        }

        adminMapper.deleteRolePermissions(command.getId());
    }

    /**
     * 根据ID查询角色
     */
    public AdminRole getById(Long id) {
        return adminRoleMapper.selectById(id);
    }

    /**
     * 分页查询角色列表
     */
    public IPage<AdminRole> queryPage(Integer page, Integer size) {
        Page<AdminRole> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<AdminRole>()
                .eq(AdminRole::getDeleted, 0)
                .orderByDesc(AdminRole::getCreatedTime);

        return adminRoleMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 查询所有角色
     */
    public List<AdminRole> queryAll() {
        return adminRoleMapper.selectList(
                new LambdaQueryWrapper<AdminRole>()
                        .eq(AdminRole::getDeleted, 0)
                        .orderByDesc(AdminRole::getCreatedTime)
        );
    }

    /**
     * 获取角色拥有的权限列表
     */
    public List<AdminPermission> getRolePermissions(Long roleId) {
        return adminMapper.selectByRole(roleId);
    }

    /**
     * 为角色分配权限
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(AdminRoleAssignPermissionCommand command) {
        AdminRole role = adminRoleMapper.selectById(command.getRoleId());
        if (role == null || role.getDeleted() == 1) {
            throw new BusinessException("角色不存在");
        }

        if (command.getPermissionIds() != null && !command.getPermissionIds().isEmpty()) {
            Long validCount = adminPermissionMapper.selectCount(
                    new LambdaQueryWrapper<AdminPermission>()
                            .in(AdminPermission::getId, command.getPermissionIds())
            );

            if (validCount != command.getPermissionIds().size()) {
                throw new BusinessException("部分权限不存在");
            }
        }

        adminMapper.deleteRolePermissions(command.getRoleId());

        if (command.getPermissionIds() != null && !command.getPermissionIds().isEmpty()) {
            adminMapper.batchInsertRolePermissions(command.getRoleId(), command.getPermissionIds());
        }
    }
}
