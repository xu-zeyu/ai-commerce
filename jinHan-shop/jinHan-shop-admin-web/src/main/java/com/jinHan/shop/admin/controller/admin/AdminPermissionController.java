package com.jinHan.shop.admin.controller.admin;

import com.aicommerce.common.model.Result;
import com.aicommerce.log.annotation.Log;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jinHan.shop.admin.controller.admin.request.AdminPermissionCreateRequest;
import com.jinHan.shop.admin.controller.admin.request.AdminPermissionUpdateRequest;
import com.jinHan.shop.core.admin.domain.handler.AdminPermissionHandler;
import com.jinHan.shop.core.admin.domain.model.AdminPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 类名: AdminPermissionController
 * 描述: 权限管理接口
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Validated
@RestController
@RequestMapping("/admin/permission")
@Tag(name = "权限管理")
public class AdminPermissionController {

    private final AdminPermissionHandler adminPermissionHandler;

    public AdminPermissionController(AdminPermissionHandler adminPermissionHandler) {
        this.adminPermissionHandler = adminPermissionHandler;
    }

    @Log(value = "创建权限", operationType = "PERMISSION_CREATE")
    @Operation(summary = "创建权限")
    @PostMapping("/create")
    public Result<Void> create(@RequestBody @Valid AdminPermissionCreateRequest request) {
        adminPermissionHandler.create(request.toCommand());
        return Result.success();
    }

    @Log(value = "更新权限", operationType = "PERMISSION_UPDATE")
    @Operation(summary = "更新权限")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid AdminPermissionUpdateRequest request) {
        adminPermissionHandler.update(request.toCommand());
        return Result.success();
    }

    @Log(value = "删除权限", operationType = "PERMISSION_DELETE")
    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        adminPermissionHandler.delete(id);
        return Result.success();
    }

    @Log(value = "查询权限详情", operationType = "PERMISSION_GET")
    @Operation(summary = "查询权限详情")
    @GetMapping("/{id}")
    public Result<AdminPermission> getById(@PathVariable Long id) {
        return Result.success(adminPermissionHandler.getById(id));
    }

    @Log(value = "分页查询权限列表", operationType = "PERMISSION_PAGE")
    @Operation(summary = "分页查询权限列表")
    @GetMapping("/page")
    public Result<IPage<AdminPermission>> queryPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adminPermissionHandler.queryPage(page, size));
    }

    @Log(value = "查询所有权限", operationType = "PERMISSION_LIST")
    @Operation(summary = "查询所有权限")
    @GetMapping("/list")
    public Result<java.util.List<AdminPermission>> queryAll() {
        return Result.success(adminPermissionHandler.queryAll());
    }
}
