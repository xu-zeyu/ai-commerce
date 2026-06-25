package com.jinHan.shop.core.supplier.domain.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 类名: SupplierBrandCreateCommand
 * 描述: 新增供应商品牌关系指令
 * 作者: xuzeyu
 * 创建时间: 2026/6/24
 */
@Data
public class SupplierBrandCreateCommand {

    /**
     * 供应商ID
     */
    @NotNull(message = "供应商ID不能为空")
    private Long supplierId;

    /**
     * 品牌ID
     */
    @NotNull(message = "品牌ID不能为空")
    private Long brandId;
}
