package com.jinHan.shop.core.goodsBrand.domain.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 类名: GoodsBrandCreateCommand
 * 描述: 新增商品品牌指令
 * 作者: xuzeyu
 * 创建时间: 2026/6/23
 */
@Data
public class GoodsBrandCreateCommand {

    @NotBlank(message = "品牌名称不能为空")
    private String name;

    private String logo;

    private String firstLetter;

    @NotNull(message = "所属分类ID不能为空")
    private Long categoryId;

    private Integer sort;

    @NotNull(message = "状态不能为空")
    private Integer status;

}
