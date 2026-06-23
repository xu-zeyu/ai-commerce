package com.jinHan.shop.core.goodsCategory.domain.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类名: GoodsCategoryUpdateCommand
 * 描述: 根据id更新商品分类内容
 * 作者: xuzeyu
 * 创建时间: 2026/6/22
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GoodsCategoryUpdateCommand extends GoodsCategoryCreateCommand{
    @NotNull(message = "分类ID不能为空")
    private Long id;

    public GoodsCategoryUpdateCommand(Long id, GoodsCategoryCreateCommand command) {
        this.setId(id);
        this.setName(command.getName());
        this.setIcon(command.getIcon());
        this.setParent_id(command.getParent_id());
        this.setLevel(command.getLevel());
        this.setSort(command.getSort());
        this.setStatus(command.getStatus());
    }
}
