package com.jinHan.shop.core.product.domain.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 类名: ProductDetails
 * 描述: 商品详情 model
 * 作者: xuzeyu
 * 创建时间: 2026/7/3
 */
@Data
public class ProductDetails {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * SPU编码，全平台唯一
     */
    private String spuCode;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品副标题
     */
    private String subTitle;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 销售状态
     */
    private SaleStatusEnum saleStatus;

    /**
     * 审核状态
     */
    private AuditStatusEnum auditStatus;

    /**
     * 创建人
     */
    private Long createdBy;

    /**
     * 更新人
     */
    private Long updatedBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;


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
