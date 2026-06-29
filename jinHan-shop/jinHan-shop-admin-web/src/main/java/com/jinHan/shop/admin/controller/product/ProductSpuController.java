package com.jinHan.shop.admin.controller.product;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.aicommerce.common.model.Result;
import com.aicommerce.log.annotation.Log;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jinHan.shop.admin.controller.product.response.ProductSpuPageResponse;
import com.jinHan.shop.core.admin.domain.constant.AdminPermissionConst;
import com.jinHan.shop.core.product.domain.command.ProductSpuCommand;
import com.jinHan.shop.core.product.domain.command.ProductSpuPageQueryCommand;
import com.jinHan.shop.core.product.domain.handler.ProductSpuCreateHandler;
import com.jinHan.shop.core.product.domain.handler.ProductSpuPageQueryHandler;
import com.jinHan.shop.core.product.domain.handler.ProductSpuUpdateHandler;
import com.jinHan.shop.core.product.domain.model.ProductSpu;
import com.jinHan.shop.core.product.domain.model.ProductSpuPageQueryResult;
import jakarta.annotation.Resource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 类名: ProductSpuController
 * 描述: 商品spu 管理
 * 作者: xuzeyu
 * 创建时间: 2026/6/25
 */
@Validated
@RestController
@RequestMapping("/product")
@Tag(name = "商品spu 管理")
public class ProductSpuController {

    @Resource
    private ProductSpuCreateHandler productSpuCreateHandler;

    @Resource
    private ProductSpuPageQueryHandler productSpuPageQueryHandler;

    @Resource
    private ProductSpuUpdateHandler productSpuUpdateHandler;

    @Log(value = "新增商品spu", operationType = "PRODUCT_SPU_CREATE")
    @Operation(summary = "新增商品spu")
    @PostMapping("/create")
    @SaCheckPermission(AdminPermissionConst.PRODUCT_SPU_CREATE)
    public Result<Long> create(@RequestBody @Valid ProductSpuCommand command) {
        return Result.success(productSpuCreateHandler.create(command));
    }

    @Log(value = "编辑商品spu", operationType = "PRODUCT_SPU_UPDATE")
    @Operation(summary = "编辑商品spu")
    @PutMapping("{id}")
    @SaCheckPermission(AdminPermissionConst.PRODUCT_SPU_UPDATE)
    public Result<ProductSpu> update(@RequestBody @Valid ProductSpuCommand command, @PathVariable Long id) {
        return Result.success(productSpuUpdateHandler.update(command,id));
    }


    @Log(value = "商品spu分页列表", operationType = "PRODUCT_SPU_PAGE")
    @Operation(summary = "商品spu分页列表")
    @GetMapping("/page")
    @SaCheckPermission(AdminPermissionConst.PRODUCT_SPU_PAGE)
    public Result<IPage<ProductSpuPageResponse>> queryPage(@Valid ProductSpuPageQueryCommand command) {
        IPage<ProductSpuPageQueryResult> page = productSpuPageQueryHandler.queryPage(command);
        return Result.success(page.convert(this::convertPageResponse));
    }

    private ProductSpuPageResponse convertPageResponse(ProductSpuPageQueryResult queryResult) {
        ProductSpuPageResponse response = new ProductSpuPageResponse();
        BeanUtils.copyProperties(queryResult, response);
        return response;
    }
}
