package com.jinHan.shop.core.supplier.domain.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类名: SupplierBrandUpdateCommand
 * 描述: 根据id更新供应商品牌关系
 * 作者: xuzeyu
 * 创建时间: 2026/6/24
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SupplierBrandUpdateCommand extends SupplierBrandCreateCommand {

    @NotNull(message = "供应商品牌关系ID不能为空")
    private Long id;

    public SupplierBrandUpdateCommand(Long id, SupplierBrandCreateCommand command) {
        this.setId(id);
        this.setSupplierId(command.getSupplierId());
        this.setBrandId(command.getBrandId());
    }
}
