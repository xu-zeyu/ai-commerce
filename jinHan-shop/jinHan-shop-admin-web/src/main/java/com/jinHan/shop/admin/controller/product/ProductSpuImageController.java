package com.jinHan.shop.admin.controller.product;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.aicommerce.common.model.Result;
import com.aicommerce.log.annotation.Log;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jinHan.shop.admin.controller.product.response.ProductSpuImagePageResponse;
import com.jinHan.shop.core.admin.domain.constant.AdminPermissionConst;
import com.jinHan.shop.core.product.domain.command.ProductSpuImageCommand;
import com.jinHan.shop.core.product.domain.command.ProductSpuImagePageQueryCommand;
import com.jinHan.shop.core.product.domain.handler.ProductSpuImageCreateHandler;
import com.jinHan.shop.core.product.domain.handler.ProductSpuImageDeleteHandler;
import com.jinHan.shop.core.product.domain.handler.ProductSpuImagePageQueryHandler;
import com.jinHan.shop.core.product.domain.handler.ProductSpuImageUpdateHandler;
import com.jinHan.shop.core.product.domain.model.ProductSpuImage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 类名: ProductSpuImageController
 * 描述: 商品spu图片 管理
 * 作者: xuzeyu
 * 创建时间: 2026/6/30
 */
@Validated
@RestController
@RequestMapping("/product/image")
@Tag(name = "商品spu图片 管理")
public class ProductSpuImageController {

    @Resource
    private ProductSpuImageCreateHandler productSpuImageCreateHandler;

    @Resource
    private ProductSpuImagePageQueryHandler productSpuImagePageQueryHandler;

    @Resource
    private ProductSpuImageUpdateHandler productSpuImageUpdateHandler;

    @Resource
    private ProductSpuImageDeleteHandler productSpuImageDeleteHandler;

    @Log(value = "新增商品spu图片", operationType = "PRODUCT_SPU_IMAGE_CREATE")
    @Operation(summary = "新增商品spu图片")
    @PostMapping("/create")
    @SaCheckPermission(AdminPermissionConst.PRODUCT_SPU_IMAGE_CREATE)
    public Result<Long> create(@RequestBody @Valid ProductSpuImageCommand command) {
        return Result.success(productSpuImageCreateHandler.create(command));
    }

    @Log(value = "编辑商品spu图片", operationType = "PRODUCT_SPU_IMAGE_UPDATE")
    @Operation(summary = "编辑商品spu图片")
    @PutMapping("{id}")
    @SaCheckPermission(AdminPermissionConst.PRODUCT_SPU_IMAGE_UPDATE)
    public Result<ProductSpuImage> update(@RequestBody @Valid ProductSpuImageCommand command, @PathVariable Long id) {
        return Result.success(productSpuImageUpdateHandler.update(command, id));
    }

    @Log(value = "商品spu图片分页列表", operationType = "PRODUCT_SPU_IMAGE_PAGE")
    @Operation(summary = "商品spu图片分页列表")
    @GetMapping("/page")
    @SaCheckPermission(AdminPermissionConst.PRODUCT_SPU_IMAGE_PAGE)
    public Result<IPage<ProductSpuImagePageResponse>> queryPage(@Valid ProductSpuImagePageQueryCommand command) {
        IPage<ProductSpuImage> page = productSpuImagePageQueryHandler.queryPage(command);
        return Result.success(page.convert(this::convertPageResponse));
    }

    @Log(value = "删除商品spu图片", operationType = "PRODUCT_SPU_IMAGE_DELETE")
    @Operation(summary = "删除商品spu图片")
    @DeleteMapping("/{id}")
    @SaCheckPermission(AdminPermissionConst.PRODUCT_SPU_IMAGE_DELETE)
    public Result<Void> delete(@PathVariable Long id) {
        productSpuImageDeleteHandler.delete(id);
        return Result.success();
    }

    private ProductSpuImagePageResponse convertPageResponse(ProductSpuImage productSpuImage) {
        ProductSpuImagePageResponse response = new ProductSpuImagePageResponse();
        BeanUtils.copyProperties(productSpuImage, response);
        return response;
    }
}
