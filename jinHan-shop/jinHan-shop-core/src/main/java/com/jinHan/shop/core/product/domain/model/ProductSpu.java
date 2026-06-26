package com.jinHan.shop.core.product.domain.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 类名: ProductSup
 * 描述: 商品 spu
 * 作者: xuzeyu
 * 创建时间: 2026/6/25
 */

@Data
@TableName("product_spu")
public class ProductSpu implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
     * 排序值
     */
    private Integer sort;

    /**
     * 销量
     */
    private Integer salesCount;

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