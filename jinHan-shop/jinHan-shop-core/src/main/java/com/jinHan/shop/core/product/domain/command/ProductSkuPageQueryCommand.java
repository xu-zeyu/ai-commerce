package com.jinHan.shop.core.product.domain.command;

import com.aicommerce.starter.mybatis.PageParam;
import com.jinHan.shop.core.product.domain.model.SkuStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类名: ProductSkuPageQueryCommand
 * 描述: 分页查询商品SKU指令
 * 作者: xuzeyu
 * 创建时间: 2026/7/1
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductSkuPageQueryCommand extends PageParam {

    /**
     * SPU ID筛选
     */
    private Long spuId;

    /**
     * SKU编码（模糊搜索）
     */
    private String skuCode;

    /**
     * 状态筛选：0-禁用 1-启用
     */
    private SkuStatusEnum status;
}
