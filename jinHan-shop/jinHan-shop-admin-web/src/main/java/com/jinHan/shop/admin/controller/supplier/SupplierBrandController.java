package com.jinHan.shop.admin.controller.supplier;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.aicommerce.common.model.Result;
import com.aicommerce.log.annotation.Log;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jinHan.shop.core.admin.domain.constant.AdminPermissionConst;
import com.jinHan.shop.core.supplier.domain.command.SupplierBrandCreateCommand;
import com.jinHan.shop.core.supplier.domain.command.SupplierBrandPageQueryCommand;
import com.jinHan.shop.core.supplier.domain.command.SupplierBrandUpdateCommand;
import com.jinHan.shop.core.supplier.domain.handler.SupplierBrandCreateHandler;
import com.jinHan.shop.core.supplier.domain.handler.SupplierBrandDeleteHandler;
import com.jinHan.shop.core.supplier.domain.handler.SupplierBrandPageQueryHandler;
import com.jinHan.shop.core.supplier.domain.handler.SupplierBrandUpdateHandler;
import com.jinHan.shop.core.supplier.domain.model.SupplierBrand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 类名: SupplierBrandController
 * 描述: 供应商品牌关系管理
 * 作者: xuzeyu
 * 创建时间: 2026/6/24
 */
@Validated
@RestController
@RequestMapping("/supplier/brand")
@Tag(name = "供应商品牌关系管理")
public class SupplierBrandController {

    @Resource
    private SupplierBrandCreateHandler supplierBrandCreateHandler;

    @Resource
    private SupplierBrandUpdateHandler supplierBrandUpdateHandler;

    @Resource
    private SupplierBrandDeleteHandler supplierBrandDeleteHandler;

    @Resource
    private SupplierBrandPageQueryHandler supplierBrandPageQueryHandler;

    @Log(value = "新增供应商品牌关系", operationType = "SUPPLIER_BRAND_CREATE")
    @Operation(summary = "新增供应商品牌关系")
    @PostMapping("/create")
    @SaCheckPermission(AdminPermissionConst.SUPPLIER_BRAND_CREATE)
    public Result<Long> create(@RequestBody @Valid SupplierBrandCreateCommand command) {
        return Result.success(supplierBrandCreateHandler.create(command));
    }

    @Log(value = "编辑供应商品牌关系", operationType = "SUPPLIER_BRAND_UPDATE")
    @Operation(summary = "编辑供应商品牌关系")
    @PutMapping("/{id}")
    @SaCheckPermission(AdminPermissionConst.SUPPLIER_BRAND_UPDATE)
    public Result<SupplierBrand> update(@PathVariable Long id, @RequestBody @Valid SupplierBrandCreateCommand command) {
        SupplierBrand supplierBrand = supplierBrandUpdateHandler.update(new SupplierBrandUpdateCommand(id, command));
        return Result.success(supplierBrand);
    }

    @Log(value = "删除供应商品牌关系", operationType = "SUPPLIER_BRAND_DELETE")
    @Operation(summary = "删除供应商品牌关系")
    @DeleteMapping("/{id}")
    @SaCheckPermission(AdminPermissionConst.SUPPLIER_BRAND_DELETE)
    public Result<Void> delete(@PathVariable Long id) {
        supplierBrandDeleteHandler.delete(id);

        return Result.success(null);
    }

    @Log(value = "供应商品牌关系分页列表", operationType = "SUPPLIER_BRAND_PAGE")
    @Operation(summary = "供应商品牌关系分页列表")
    @GetMapping("/page")
    @SaCheckPermission(AdminPermissionConst.SUPPLIER_BRAND_PAGE)
    public Result<IPage<SupplierBrand>> queryPage(@Valid SupplierBrandPageQueryCommand command) {
        return Result.success(supplierBrandPageQueryHandler.queryPage(command));
    }
}
