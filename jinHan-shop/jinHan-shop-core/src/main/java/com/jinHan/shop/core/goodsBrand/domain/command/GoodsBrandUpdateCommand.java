package com.jinHan.shop.core.goodsBrand.domain.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类名: GoodsBrandUpdateCommand
 * 描述: 根据id更新商品品牌内容
 * 作者: xuzeyu
 * 创建时间: 2026/6/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GoodsBrandUpdateCommand extends GoodsBrandCreateCommand {

    @NotNull(message = "品牌ID不能为空")
    private Long id;

    public GoodsBrandUpdateCommand(Long id, GoodsBrandCreateCommand command) {
        this.setId(id);
        this.setName(command.getName());
        this.setLogo(command.getLogo());
        this.setFirstLetter(command.getFirstLetter());
        this.setCategoryId(command.getCategoryId());
        this.setSort(command.getSort());
        this.setStatus(command.getStatus());
    }

}
