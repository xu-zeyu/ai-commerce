package com.jinHan.shop.core.goodsCategory.domain.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 类名: GoodsCategoryCreateCommand
 * 描述: 新增商品分类指令
 * 作者: xuzeyu
 * 创建时间: 2026/6/15
 */
@Data
public class GoodsCategoryCreateCommand {
    @NotNull(message = "父级 ID不能为空")
    private Long parent_id;
    @NotNull(message = "分类名称不能为空")
    private String name;
    @NotNull(message = "分类图标不能为空")
    private String icon;
    @NotNull(message = "分类层级不能为空")
    private Integer level;
    private Integer sort;
    private int status;
}
