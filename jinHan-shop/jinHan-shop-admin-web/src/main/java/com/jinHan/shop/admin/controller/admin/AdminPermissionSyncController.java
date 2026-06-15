package com.jinHan.shop.admin.controller.admin;

import com.aicommerce.common.model.Result;
import com.aicommerce.log.annotation.Log;
import com.jinHan.shop.core.admin.domain.command.AdminPermissionBatchSyncCommand;
import com.jinHan.shop.core.admin.domain.handler.AdminPermissionBatchSyncHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类名: AdminPermissionSyncController
 * 描述: 权限同步管理接口
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Validated
@RestController
@RequestMapping("/admin/permission")
@Tag(name = "权限同步管理")
public class AdminPermissionSyncController {

    private final AdminPermissionBatchSyncHandler adminPermissionBatchSyncHandler;

    public AdminPermissionSyncController(AdminPermissionBatchSyncHandler adminPermissionBatchSyncHandler) {
        this.adminPermissionBatchSyncHandler = adminPermissionBatchSyncHandler;
    }

    /**
     * 批量同步权限：将前端定义的权限列表同步到后端数据库
     */
    @Log(value = "批量同步权限", operationType = "PERMISSION_SYNC")
    @Operation(summary = "批量同步权限")
    @PostMapping("/batchSync")
    public Result<Void> batchSync(@RequestBody @Valid AdminPermissionBatchSyncCommand request) {
        adminPermissionBatchSyncHandler.batchSync(request);
        return Result.success();
    }
}
