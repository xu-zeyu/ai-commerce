package com.jinHan.shop.core.product.domain.command;

import com.aicommerce.starter.mybatis.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类名: ProductSpuPageQueryCommand
 * 描述: 分页查询商品SPU指令
 * 作者: xuzeyu
 * 创建时间: 2026/6/26
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductSpuPageQueryCommand extends PageParam {

    /**
     * 商品名称（模糊搜索）
     */
    private String name;

    /**
     * SPU编码（模糊搜索）
     */
    private String spuCode;

    /**
     * 供应商ID筛选
     */
    private Long supplierId;

    /**
     * 分类ID筛选
     */
    private Long categoryId;

    /**
     * 品牌ID筛选
     */
    private Long brandId;

    /**
     * 销售状态筛选：0-下架 1-上架
     */
    private Integer saleStatus;

    /**
     * 审核状态筛选：0-待审核 1-审核通过 2-审核拒绝
     */
    private Integer auditStatus;

}
