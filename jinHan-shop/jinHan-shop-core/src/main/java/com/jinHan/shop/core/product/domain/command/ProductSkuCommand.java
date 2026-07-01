package com.jinHan.shop.core.product.domain.command;

import com.jinHan.shop.core.product.domain.model.SkuStatusEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 类名: ProductSkuCommand
 * 描述: 商品SKU新增/编辑指令
 * 作者: xuzeyu
 * 创建时间: 2026/7/1
 */
@Data
public class ProductSkuCommand {

    /**
     * SPU ID
     */
    @NotNull(message = "SPU ID不能为空")
    private Long spuId;

    /**
     * SKU编码
     */
    @NotBlank(message = "SKU编码不能为空")
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
    @NotNull(message = "售价不能为空")
    @DecimalMin(value = "0.01", message = "售价必须大于0")
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 库存数量
     */
    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能为负数")
    private Integer stock;

    /**
     * 状态 0-禁用 1-启用
     */
    private SkuStatusEnum status;
}
