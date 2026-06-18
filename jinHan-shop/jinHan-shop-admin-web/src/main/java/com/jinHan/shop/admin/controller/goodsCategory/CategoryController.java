package com.jinHan.shop.admin.controller.goodsCategory;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.aicommerce.common.model.Result;
import com.aicommerce.log.annotation.Log;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jinHan.shop.core.admin.domain.constant.AdminPermissionConst;
import com.jinHan.shop.core.goodsCategory.domain.command.GoodsCategoryCreateCommand;
import com.jinHan.shop.core.goodsCategory.domain.command.GoodsCategoryPageQueryCommand;
import com.jinHan.shop.core.goodsCategory.domain.handler.GoodsCategoryCreateHandler;
import com.jinHan.shop.core.goodsCategory.domain.handler.GoodsCategoryPageQueryHandler;
import com.jinHan.shop.core.goodsCategory.domain.handler.GoodsCategoryTreeHandler;
import com.jinHan.shop.core.goodsCategory.domain.model.GoodsCategory;
import com.jinHan.shop.core.goodsCategory.domain.model.GoodsCategoryTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 类名: categoryController
 * 描述: 商品分类管理
 * 作者: xuzeyu
 * 创建时间: 2026/6/15
 */
@Validated
@RestController
@RequestMapping("/goods/category")
@Tag(name = "商品分类管理")
public class CategoryController {
    @Resource
    private GoodsCategoryCreateHandler goodsCategoryCreateHandler;

    @Resource
    private GoodsCategoryPageQueryHandler goodsCategoryPageQueryHandler;

    @Resource
    private GoodsCategoryTreeHandler goodsCategoryTreeHandler;

    @Log(value = "新增商品分类", operationType = "GOODSCATEGORY_CREATE")
    @Operation(summary = "新增商品分类")
    @PostMapping("/create")
    @SaCheckPermission(AdminPermissionConst.GOODS_CATEGORY_CREATE)
    public Result<Long> create(@RequestBody @Valid GoodsCategoryCreateCommand command) {
        return Result.success(goodsCategoryCreateHandler.create(command));
    }


    @Log(value = "商品分类层级列表", operationType = "GOODSCATEGORY_LIST")
    @Operation(summary = "商品分类层级列表")
    @GetMapping("/page")
    @SaCheckPermission(AdminPermissionConst.GOODS_CATEGORY_PAGE)
    public Result<IPage<GoodsCategory>> queryPage(@Valid GoodsCategoryPageQueryCommand command) {
        return Result.success(goodsCategoryPageQueryHandler.queryPage(command));
    }


    @Log(value = "商品分类树形列表", operationType = "GOODSCATEGORY_TREE")
    @Operation(summary = "商品分类树形列表")
    @GetMapping("/tree")
    @SaCheckPermission(AdminPermissionConst.GOODS_CATEGORY_TREE)
    public Result<List<GoodsCategoryTreeVO>> tree() {
        return Result.success(goodsCategoryTreeHandler.treeList());
    }
}
