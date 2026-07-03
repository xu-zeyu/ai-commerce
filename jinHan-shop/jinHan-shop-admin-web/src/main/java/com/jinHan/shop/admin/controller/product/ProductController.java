package com.jinHan.shop.admin.controller.product;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.aicommerce.common.model.Result;
import com.aicommerce.log.annotation.Log;
import com.jinHan.shop.admin.controller.product.response.ProductResponse;
import com.jinHan.shop.core.admin.domain.constant.AdminPermissionConst;
import com.jinHan.shop.core.product.domain.command.ProductDetailsCommand;
import com.jinHan.shop.core.product.domain.handler.ProductAuditHandler;
import com.jinHan.shop.core.product.domain.handler.ProductDetailsHandler;
import com.jinHan.shop.core.product.domain.model.ProductDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 类名: ProductController
 * 描述: 商品管理
 * 作者: xuzeyu
 * 创建时间: 2026/7/2
 */
@Validated
@RestController
@RequestMapping("/product")
@Tag(name = "商品管理")
public class ProductController {

    @Resource
    private ProductDetailsHandler productDetailsHandler;

    @Resource
    private ProductAuditHandler productAuditHandler;

    @Log(value = "商品详情", operationType = "PRODUCT_PAGE_DETAILS")
    @Operation(summary = "商品详情")
    @GetMapping("{id}")
    @SaCheckPermission(AdminPermissionConst.PRODUCT_PAGE_DETAILS)
    public Result<ProductResponse> details(@PathVariable Long id) {
        ProductDetailsCommand command = new ProductDetailsCommand(id);
        ProductDetails productDetails = productDetailsHandler.details(command);
        ProductResponse productResponse = new ProductResponse();
        return Result.success(productResponse.toProductDetails(productDetails));
    }

    @Log(value = "商品审核", operationType = "PRODUCT_PAGE_AUDIT")
    @Operation(summary = "商品审核")
    @PutMapping("/audit/{id}")
    @SaCheckPermission(AdminPermissionConst.PRODUCT_PAGE_AUDIT)
    public Result<ProductResponse> audit(@PathVariable Long id) {
        ProductDetails productDetails = productAuditHandler.audit(id);
        ProductResponse productResponse = new ProductResponse();
        return Result.success(productResponse.toProductDetails(productDetails));
    }

}
