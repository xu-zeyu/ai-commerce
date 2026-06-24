package com.jinHan.shop.admin.controller.goodsBrand;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.aicommerce.common.model.Result;
import com.aicommerce.log.annotation.Log;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jinHan.shop.core.admin.domain.constant.AdminPermissionConst;
import com.jinHan.shop.core.goodsBrand.domain.command.GoodsBrandCreateCommand;
import com.jinHan.shop.core.goodsBrand.domain.command.GoodsBrandPageQueryCommand;
import com.jinHan.shop.core.goodsBrand.domain.command.GoodsBrandUpdateCommand;
import com.jinHan.shop.core.goodsBrand.domain.handler.GoodsBrandCreateHandler;
import com.jinHan.shop.core.goodsBrand.domain.handler.GoodsBrandDeleteHandler;
import com.jinHan.shop.core.goodsBrand.domain.handler.GoodsBrandPageQueryHandler;
import com.jinHan.shop.core.goodsBrand.domain.handler.GoodsBrandUpdateHandler;
import com.jinHan.shop.core.goodsBrand.domain.model.GoodsBrand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 类名: BrandController
 * 描述: 商品品牌管理
 * 作者: xuzeyu
 * 创建时间: 2026/6/23
 */
@Validated
@RestController
@RequestMapping("/goods/brand")
@Tag(name = "商品品牌管理")
public class BrandController {

    @Resource
    private GoodsBrandCreateHandler goodsBrandCreateHandler;

    @Resource
    private GoodsBrandUpdateHandler goodsBrandUpdateHandler;

    @Resource
    private GoodsBrandDeleteHandler goodsBrandDeleteHandler;

    @Resource
    private GoodsBrandPageQueryHandler goodsBrandPageQueryHandler;

    @Log(value = "新增商品品牌", operationType = "GOODS_BRAND_CREATE")
    @Operation(summary = "新增商品品牌")
    @PostMapping("/create")
    @SaCheckPermission(AdminPermissionConst.GOODS_BRAND_CREATE)
    public Result<Long> create(@RequestBody @Valid GoodsBrandCreateCommand command) {
        return Result.success(goodsBrandCreateHandler.create(command));
    }

    @Log(value = "编辑商品品牌", operationType = "GOODS_BRAND_UPDATE")
    @Operation(summary = "编辑商品品牌")
    @PutMapping("/{id}")
    @SaCheckPermission(AdminPermissionConst.GOODS_BRAND_UPDATE)
    public Result<GoodsBrand> update(@PathVariable Long id, @RequestBody @Valid GoodsBrandCreateCommand command) {
        GoodsBrand goodsBrand = goodsBrandUpdateHandler.update(new GoodsBrandUpdateCommand(id, command));
        return Result.success(goodsBrand);
    }

    @Log(value = "删除商品品牌", operationType = "GOODS_BRAND_DELETE")
    @Operation(summary = "删除商品品牌")
    @DeleteMapping("/{id}")
    @SaCheckPermission(AdminPermissionConst.GOODS_BRAND_DELETE)
    public Result<Void> delete(@PathVariable Long id) {
        goodsBrandDeleteHandler.delete(id);
        return Result.success(null);
    }

    @Log(value = "商品品牌分页列表", operationType = "GOODS_BRAND_PAGE")
    @Operation(summary = "商品品牌分页列表")
    @GetMapping("/page")
    @SaCheckPermission(AdminPermissionConst.GOODS_BRAND_PAGE)
    public Result<IPage<GoodsBrand>> queryPage(@Valid GoodsBrandPageQueryCommand command) {
        return Result.success(goodsBrandPageQueryHandler.queryPage(command));
    }

}
