package com.jinHan.shop.core.product.domain.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 类名: ProductSpuCreateCommand
 * 描述: 新增商品SPU指令
 * 作者: xuzeyu
 * 创建时间: 2026/6/25
 */
@Data
public class ProductSpuCreateCommand {

    /**
     * 供应商ID
     */
    @NotNull(message = "供应商ID不能为空")
    private Long supplierId;

    /**
     * SPU编码，全平台唯一
     */
    @NotBlank(message = "SPU编码不能为空")
    private String spuCode;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 商品副标题
     */
    private String subTitle;

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 品牌ID
     */
    @NotNull(message = "品牌ID不能为空")
    private Long brandId;

    /**
     * 销售状态：0-下架 1-上架
     */
    @NotNull(message = "销售状态不能为空")
    private Integer saleStatus;

    /**
     * 审核状态：0-待审核 1-审核通过 2-审核拒绝
     */
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 销量
     */
    private Integer salesCount;
}
