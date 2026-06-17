package com.jinHan.shop.core.goodsCategory.domain.command;

import com.aicommerce.starter.mybatis.PageParam;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类名: GoodsCategoryPageQueryCommand
 * 描述: 分页查询商品分类
 * 作者: xuzeyu
 * 创建时间: 2026/6/17
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GoodsCategoryPageQueryCommand extends PageParam {
    @NotNull(message = "父级 id 为必传, 0->一级分类列表")
    private Long parentId;

    private String name;
}
