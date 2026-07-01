package com.jinHan.shop.core.product.domain.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 类名: ProductSku
 * 描述: 商品 SKU
 * 作者: xuzeyu
 * 创建时间: 2026/7/1
 */
@Data
@TableName("product_sku")
public class ProductSku implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
     * 逻辑删除标识
     * 0-未删除
     */
    @TableLogic
    private Long deleted;

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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
