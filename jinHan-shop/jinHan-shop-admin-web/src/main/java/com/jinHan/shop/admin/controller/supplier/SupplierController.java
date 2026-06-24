package com.jinHan.shop.admin.controller.supplier;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.aicommerce.common.model.Result;
import com.aicommerce.log.annotation.Log;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jinHan.shop.core.admin.domain.constant.AdminPermissionConst;
import com.jinHan.shop.core.supplier.domain.command.SupplierCreateCommand;
import com.jinHan.shop.core.supplier.domain.command.SupplierPageQueryCommand;
import com.jinHan.shop.core.supplier.domain.command.SupplierUpdateCommand;
import com.jinHan.shop.core.supplier.domain.handler.SupplierCreateHandler;
import com.jinHan.shop.core.supplier.domain.handler.SupplierDeleteHandler;
import com.jinHan.shop.core.supplier.domain.handler.SupplierPageQueryHandler;
import com.jinHan.shop.core.supplier.domain.handler.SupplierUpdateHandler;
import com.jinHan.shop.core.supplier.domain.model.Supplier;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 类名: SupplierController
 * 描述: 供应商管理
 * 作者: xuzeyu
 * 创建时间: 2026/6/24
 */
@Validated
@RestController
@RequestMapping("/supplier")
@Tag(name = "供应商管理")
public class SupplierController {

    @Resource
    private SupplierCreateHandler supplierCreateHandler;

    @Resource
    private SupplierUpdateHandler supplierUpdateHandler;

    @Resource
    private SupplierDeleteHandler supplierDeleteHandler;

    @Resource
    private SupplierPageQueryHandler supplierPageQueryHandler;

    @Log(value = "新增供应商", operationType = "SUPPLIER_CREATE")
    @Operation(summary = "新增供应商")
    @PostMapping("/create")
    @SaCheckPermission(AdminPermissionConst.SUPPLIER_CREATE)
    public Result<Long> create(@RequestBody @Valid SupplierCreateCommand command) {
        return Result.success(supplierCreateHandler.create(command));
    }

    @Log(value = "编辑供应商", operationType = "SUPPLIER_UPDATE")
    @Operation(summary = "编辑供应商")
    @PutMapping("/{id}")
    @SaCheckPermission(AdminPermissionConst.SUPPLIER_UPDATE)
    public Result<Supplier> update(@PathVariable Long id, @RequestBody @Valid SupplierCreateCommand command) {
        Supplier supplier = supplierUpdateHandler.update(new SupplierUpdateCommand(id, command));
        return Result.success(supplier);
    }

    @Log(value = "删除供应商", operationType = "SUPPLIER_DELETE")
    @Operation(summary = "删除供应商")
    @DeleteMapping("/{id}")
    @SaCheckPermission(AdminPermissionConst.SUPPLIER_DELETE)
    public Result<Void> delete(@PathVariable Long id) {
        supplierDeleteHandler.delete(id);
        return Result.success(null);
    }

    @Log(value = "供应商分页列表", operationType = "SUPPLIER_PAGE")
    @Operation(summary = "供应商分页列表")
    @GetMapping("/page")
    @SaCheckPermission(AdminPermissionConst.SUPPLIER_PAGE)
    public Result<IPage<Supplier>> queryPage(@Valid SupplierPageQueryCommand command) {
        return Result.success(supplierPageQueryHandler.queryPage(command));
    }

}
