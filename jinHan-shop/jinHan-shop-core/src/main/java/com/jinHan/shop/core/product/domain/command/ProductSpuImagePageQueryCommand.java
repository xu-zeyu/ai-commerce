package com.jinHan.shop.core.product.domain.command;

import com.aicommerce.starter.mybatis.PageParam;
import com.jinHan.shop.core.product.domain.model.ImageTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类名: ProductSpuImagePageQueryCommand
 * 描述: 分页查询商品SPU图片指令
 * 作者: xuzeyu
 * 创建时间: 2026/6/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductSpuImagePageQueryCommand extends PageParam {

    /**
     * SPU ID筛选
     */
    private Long spuId;

    /**
     * 图片类型筛选：1-主图 2-轮播图 3-详情图
     */
    private ImageTypeEnum imageType;
}
