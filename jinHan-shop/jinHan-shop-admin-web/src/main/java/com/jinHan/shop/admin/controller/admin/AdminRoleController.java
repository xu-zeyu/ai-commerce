package com.jinHan.shop.admin.controller.admin;

import com.aicommerce.common.model.Result;
import com.aicommerce.log.annotation.Log;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jinHan.shop.admin.controller.admin.request.AdminRoleAssignPermissionRequest;
import com.jinHan.shop.admin.controller.admin.request.AdminRoleCreateRequest;
import com.jinHan.shop.admin.controller.admin.request.AdminRoleDeleteRequest;
import com.jinHan.shop.admin.controller.admin.request.AdminRoleUpdateRequest;
import com.jinHan.shop.core.admin.domain.handler.AdminRoleHandler;
import com.jinHan.shop.core.admin.domain.model.AdminPermission;
import com.jinHan.shop.core.admin.domain.model.AdminRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 类名: AdminRoleController
 * 描述: 管理员角色管理接口
 * 作者: xuzeyu
 * 创建时间: 2026/1/21
 */
@Validated
@RestController
@RequestMapping("/admin/role")
@Tag(name = "角色管理")
public class AdminRoleController {

    private final AdminRoleHandler adminRoleHandler;

    public AdminRoleController(AdminRoleHandler adminRoleHandler) {
        this.adminRoleHandler = adminRoleHandler;
    }

    @Log(value = "创建角色", operationType = "ROLE_CREATE")
    @Operation(summary = "创建角色")
    @PostMapping("/create")
    public Result<Void> create(@RequestBody @Valid AdminRoleCreateRequest request) {
        adminRoleHandler.create(request.toCommand());
        return Result.success();
    }

    @Log(value = "更新角色", operationType = "ROLE_UPDATE")
    @Operation(summary = "更新角色")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid AdminRoleUpdateRequest request) {
        adminRoleHandler.update(request.toCommand());
        return Result.success();
    }

    @Log(value = "删除角色", operationType = "ROLE_DELETE")
    @Operation(summary = "删除角色")
    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody @Valid AdminRoleDeleteRequest request) {
        adminRoleHandler.delete(request.toCommand());
        return Result.success();
    }

    @Log(value = "查询角色详情", operationType = "ROLE_GET")
    @Operation(summary = "查询角色详情")
    @GetMapping("/{id}")
    public Result<AdminRole> getById(@PathVariable Long id) {
        return Result.success(adminRoleHandler.getById(id));
    }

    @Log(value = "分页查询角色列表", operationType = "ROLE_PAGE")
    @Operation(summary = "分页查询角色列表")
    @GetMapping("/page")
    public Result<IPage<AdminRole>> queryPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adminRoleHandler.queryPage(page, size));
    }

    @Log(value = "查询所有角色", operationType = "ROLE_LIST")
    @Operation(summary = "查询所有角色")
    @GetMapping("/list")
    public Result<java.util.List<AdminRole>> queryAll() {
        return Result.success(adminRoleHandler.queryAll());
    }

    @Log(value = "获取角色权限列表", operationType = "ROLE_PERMISSIONS")
    @Operation(summary = "获取角色权限列表")
    @GetMapping("/{id}/permissions")
    public Result<List<AdminPermission>> getRolePermissions(@PathVariable Long id) {
        return Result.success(adminRoleHandler.getRolePermissions(id));
    }

    @Log(value = "为角色分配权限", operationType = "ROLE_ASSIGN_PERMISSIONS")
    @Operation(summary = "为角色分配权限")
    @PostMapping("/assignPermissions")
    public Result<Void> assignPermissions(@RequestBody @Valid AdminRoleAssignPermissionRequest request) {
        adminRoleHandler.assignPermissions(request.toCommand());
        return Result.success();
    }
}
