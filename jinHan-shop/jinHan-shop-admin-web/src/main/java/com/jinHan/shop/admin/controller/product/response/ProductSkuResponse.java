package com.jinHan.shop.admin.controller.product.response;

import com.jinHan.shop.core.product.domain.model.SkuStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 类名: ProductSkuResponse
 * 描述: 商品SKU响应
 * 作者: xuzeyu
 * 创建时间: 2026/7/1
 */
@Data
public class ProductSkuResponse {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * SKU编码
     */
    private String skuCode;

    /**
     * 规格信息JSON
     */
    private String specInfo;

    /**
     * SKU图片地址
     */
    private String image;

    /**
     * 售价
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 销量
     */
    private Integer salesCount;

    /**
     * 状态 0-禁用 1-启用
     */
    private SkuStatusEnum status;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}
