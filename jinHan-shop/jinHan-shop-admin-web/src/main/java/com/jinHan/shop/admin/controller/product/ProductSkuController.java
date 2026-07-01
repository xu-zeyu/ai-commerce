package com.jinHan.shop.admin.controller.product;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.aicommerce.common.model.Result;
import com.aicommerce.log.annotation.Log;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jinHan.shop.admin.controller.product.response.ProductSkuResponse;
import com.jinHan.shop.core.admin.domain.constant.AdminPermissionConst;
import com.jinHan.shop.core.product.domain.command.ProductSkuCommand;
import com.jinHan.shop.core.product.domain.command.ProductSkuPageQueryCommand;
import com.jinHan.shop.core.product.domain.handler.ProductSkuCreateHandler;
import com.jinHan.shop.core.product.domain.handler.ProductSkuDeleteHandler;
import com.jinHan.shop.core.product.domain.handler.ProductSkuPageQueryHandler;
import com.jinHan.shop.core.product.domain.handler.ProductSkuUpdateHandler;
import com.jinHan.shop.core.product.domain.model.ProductSku;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 类名: ProductSkuController
 * 描述: 商品SKU 管理
 * 作者: xuzeyu
 * 创建时间: 2026/7/1
 */
@Validated
@RestController
@RequestMapping("/product/sku")
@Tag(name = "商品SKU 管理")
public class ProductSkuController {

    @Resource
    private ProductSkuCreateHandler productSkuCreateHandler;

    @Resource
    private ProductSkuPageQueryHandler productSkuPageQueryHandler;

    @Resource
    private ProductSkuUpdateHandler productSkuUpdateHandler;

    @Resource
    private ProductSkuDeleteHandler productSkuDeleteHandler;

    @Log(value = "新增商品SKU", operationType = "PRODUCT_SKU_CREATE")
    @Operation(summary = "新增商品SKU")
    @PostMapping("/create")
    @SaCheckPermission(AdminPermissionConst.PRODUCT_SKU_CREATE)
    public Result<Long> create(@RequestBody @Valid ProductSkuCommand command) {
        return Result.success(productSkuCreateHandler.create(command));
    }

    @Log(value = "编辑商品SKU", operationType = "PRODUCT_SKU_UPDATE")
    @Operation(summary = "编辑商品SKU")
    @PutMapping("/{id}")
    @SaCheckPermission(AdminPermissionConst.PRODUCT_SKU_UPDATE)
    public Result<ProductSkuResponse> update(@RequestBody @Valid ProductSkuCommand command, @PathVariable Long id) {
        ProductSku productSku = productSkuUpdateHandler.update(command, id);
        return Result.success(convertToResponse(productSku));
    }

    @Log(value = "商品SKU分页列表", operationType = "PRODUCT_SKU_PAGE")
    @Operation(summary = "商品SKU分页列表")
    @GetMapping("/page")
    @SaCheckPermission(AdminPermissionConst.PRODUCT_SKU_PAGE)
    public Result<IPage<ProductSkuResponse>> queryPage(@Valid ProductSkuPageQueryCommand command) {
        IPage<ProductSku> page = productSkuPageQueryHandler.queryPage(command);
        return Result.success(page.convert(this::convertToResponse));
    }

    @Log(value = "删除商品SKU", operationType = "PRODUCT_SKU_DELETE")
    @Operation(summary = "删除商品SKU")
    @DeleteMapping("/{id}")
    @SaCheckPermission(AdminPermissionConst.PRODUCT_SKU_DELETE)
    public Result<Void> delete(@PathVariable Long id) {
        productSkuDeleteHandler.delete(id);
        return Result.success();
    }

    private ProductSkuResponse convertToResponse(ProductSku productSku) {
        ProductSkuResponse response = new ProductSkuResponse();
        BeanUtils.copyProperties(productSku, response);
        return response;
    }
}
