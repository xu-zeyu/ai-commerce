package com.jinHan.shop.core.product.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 类名: ProductSpuPageQueryResult
 * 描述: 商品SPU分页查询结果
 * 作者: xuzeyu
 * 创建时间: 2026/6/26
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductSpuPageQueryResult extends ProductSpu {

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 品牌名称
     */
    private String brandName;


    /**
     * 主图
     */
    private List<String> mainImage;
}
