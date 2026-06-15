package com.jinHan.shop.core.admin.domain.handler;

import com.aicommerce.auth.utils.PasswordService;
import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jinHan.shop.core.admin.domain.command.AdminCreateCommand;
import com.jinHan.shop.core.admin.domain.mapper.AdminMapper;
import com.jinHan.shop.core.admin.domain.mapper.AdminRoleMapper;
import com.jinHan.shop.core.admin.domain.model.Admin;
import com.jinHan.shop.core.admin.domain.model.AdminRole;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 类名: AdminCreateHandler
 * 描述: 创建管理员业务处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/15
 */
@Component
public class AdminCreateHandler {

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private AdminRoleMapper adminRoleMapper;

    @Resource
    private PasswordService passwordService;

    /**
     * 创建管理员
     */
    public void create(AdminCreateCommand command) {
        // 校验用户名是否已存在
        Long count = adminMapper.selectCount(
                new LambdaQueryWrapper<Admin>()
                        .eq(Admin::getUsername, command.getUsername())
        );

        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 校验角色是否存在
        if (command.getRoleId() != null) {
            Long roleExists = adminRoleMapper.selectCount(
                    new LambdaQueryWrapper<AdminRole>()
                            .eq(AdminRole::getId, command.getRoleId())
                            .eq(AdminRole::getDeleted, 0)
            );
            if (roleExists <= 0) {
                throw new BusinessException("角色不存在");
            }
        }

        Admin admin = new Admin();
        admin.setUsername(command.getUsername());
        admin.setPassword(passwordService.passwordEncrypt(command.getPassword()));
        admin.setPhone(command.getPhone());
        admin.setRealName(command.getRealName());
        admin.setRoleId(command.getRoleId());
        admin.setCreatedTime(LocalDateTime.now());
        admin.setUpdatedTime(LocalDateTime.now());

        int inserted = adminMapper.insert(admin);
        if (inserted <= 0) {
            throw new BusinessException("创建管理员失败");
        }
    }
}
