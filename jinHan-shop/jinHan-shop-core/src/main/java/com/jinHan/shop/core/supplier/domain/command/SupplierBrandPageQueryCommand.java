package com.jinHan.shop.core.supplier.domain.command;

import com.aicommerce.starter.mybatis.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类名: SupplierBrandPageQueryCommand
 * 描述: 分页查询供应商品牌关系指令
 * 作者: xuzeyu
 * 创建时间: 2026/6/24
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SupplierBrandPageQueryCommand extends PageParam {

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 品牌ID
     */
    private Long brandId;
}
