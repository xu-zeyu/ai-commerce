package com.jinHan.shop.core.admin.domain.command;

import lombok.Data;
import java.util.List;

/**
 * 类名: AdminPermissionBatchSyncCommand
 * 描述: 批量同步权限命令
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Data
public class AdminPermissionBatchSyncCommand {
    private List<PermissionItem> permissions;

    @Data
    public static class PermissionItem {
        private String name;
        private String code;
    }
}
