package com.jinHan.shop.admin.controller.product.response;

import com.jinHan.shop.core.product.domain.model.AuditStatusEnum;
import com.jinHan.shop.core.product.domain.model.SaleStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 类名: ProductSpuPageResponse
 * 描述: 商品SPU分页响应
 * 作者: xuzeyu
 * 创建时间: 2026/6/26
 */
@Data
public class ProductSpuPageResponse {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * SPU编码
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
     * 分类名称
     */
    private String categoryName;

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 销售状态
     */
    private SaleStatusEnum saleStatus;

    /**
     * 审核状态
     */
    private AuditStatusEnum auditStatus;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 销量
     */
    private Integer salesCount;

    /**
     * 主图
     */
    private List<String> mainImage;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}
