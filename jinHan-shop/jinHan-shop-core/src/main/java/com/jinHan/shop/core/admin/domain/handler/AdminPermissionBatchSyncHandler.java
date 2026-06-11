package com.jinHan.shop.core.admin.domain.handler;

import com.aicommerce.common.exception.BusinessException;
import com.jinHan.shop.core.admin.domain.command.AdminPermissionBatchSyncCommand;
import com.jinHan.shop.core.admin.domain.mapper.AdminPermissionMapper;
import com.jinHan.shop.core.admin.domain.model.AdminPermission;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 类名: AdminPermissionBatchSyncHandler
 * 描述: 批量同步权限处理器
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Component
public class AdminPermissionBatchSyncHandler {

    @Resource
    private AdminPermissionMapper adminPermissionMapper;

    /**
     * 批量同步权限
     * 如果权限已存在则跳过，不存在则创建
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchSync(AdminPermissionBatchSyncCommand command) {
        if (command.getPermissions() == null || command.getPermissions().isEmpty()) {
            throw new BusinessException("权限列表不能为空");
        }

        List<String> existingCodes = adminPermissionMapper.selectList(null)
                .stream()
                .map(AdminPermission::getCode)
                .toList();

        List<AdminPermission> newPermissions = command.getPermissions().stream()
                .filter(item -> !existingCodes.contains(item.getCode()))
                .map(item -> {
                    AdminPermission permission = new AdminPermission();
                    permission.setName(item.getName());
                    permission.setCode(item.getCode());
                    return permission;
                })
                .toList();

        if (!newPermissions.isEmpty()) {
            for (AdminPermission permission : newPermissions) {
                int inserted = adminPermissionMapper.insert(permission);
                if (inserted <= 0) {
                    throw new BusinessException("批量同步权限失败");
                }
            }
        }
    }
}
