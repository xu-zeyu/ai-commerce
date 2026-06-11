package com.jinHan.shop.admin.controller.admin;

import cn.dev33.satoken.stp.StpUtil;
import com.aicommerce.common.model.Result;
import com.aicommerce.log.annotation.Log;
import com.jinHan.shop.admin.controller.admin.response.AdminInfoResponse;
import com.jinHan.shop.core.admin.domain.mapper.AdminMapper;
import com.jinHan.shop.core.admin.domain.model.Admin;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 类名: AdminController
 * 描述: 管理员当前账号信息接口
 * 作者: xuzeyu
 * 创建时间: 2026/1/2
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminMapper adminMapper;

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
    @GetMapping("/info")
    public Result<AdminInfoResponse> info() {
        Admin admin = adminMapper.selectById(StpUtil.getLoginId().toString());
        AdminInfoResponse adminInfoResponse = new AdminInfoResponse(admin, adminMapper.selectByRoleName(admin.getRoleId()));
        return Result.success(adminInfoResponse);
    }
}
