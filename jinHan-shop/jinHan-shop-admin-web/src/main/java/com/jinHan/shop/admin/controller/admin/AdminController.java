package com.jinHan.shop.admin.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.aicommerce.common.model.Result;
import com.aicommerce.log.annotation.Log;
import com.jinHan.shop.admin.controller.admin.request.AdminCreateRequest;
import com.jinHan.shop.admin.controller.admin.response.AdminInfoResponse;
import com.jinHan.shop.core.admin.domain.handler.AdminCreateHandler;
import com.jinHan.shop.core.admin.domain.mapper.AdminMapper;
import com.jinHan.shop.core.admin.domain.model.Admin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 类名: AdminController
 * 描述: 管理员账号管理接口
 * 作者: xuzeyu
 * 创建时间: 2026/1/2
 */
@Validated
@RestController
@RequestMapping("/admin")
@Tag(name = "管理员账号管理")
public class AdminController {

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private AdminCreateHandler adminCreateHandler;

    @Operation(summary = "获取当前管理员基础信息")
    @GetMapping("/self")
    public Result<Map<String, Object>> self() {
        Map<String, Object> result = new HashMap<>();
        Admin admin = adminMapper.selectById(StpUtil.getLoginId().toString());
        result.put("avatar", admin.getAvatar());
        result.put("userName", admin.getUsername());
        result.put("authorities", StpUtil.getPermissionList());
        return Result.success(result);
    }

    @Log(value = "获取管理员详细信息", operationType = "ADMIN_INFO")
    @Operation(summary = "获取管理员详细信息")
    @GetMapping("/info")
    public Result<AdminInfoResponse> info() {
        Admin admin = adminMapper.selectById(StpUtil.getLoginId().toString());
        AdminInfoResponse adminInfoResponse = new AdminInfoResponse(admin, adminMapper.selectByRoleName(admin.getRoleId()));
        return Result.success(adminInfoResponse);
    }

    @Log(value = "创建管理员", operationType = "ADMIN_CREATE")
    @Operation(summary = "创建管理员")
    @PostMapping("/create")
    @SaCheckPermission("SUB_ADMIN")
    public Result<Void> create(@RequestBody @Valid AdminCreateRequest request) {
        adminCreateHandler.create(request.toCommand());
        return Result.success();
    }
}
