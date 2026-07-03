package com.jinHan.shop.core.product.domain.mapper;

import com.aicommerce.starter.mybatis.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinHan.shop.core.product.domain.command.ProductDetailsCommand;
import com.jinHan.shop.core.product.domain.command.ProductSpuPageQueryCommand;
import com.jinHan.shop.core.product.domain.model.ProductDetails;
import com.jinHan.shop.core.product.domain.model.ProductSpu;
import com.jinHan.shop.core.product.domain.model.ProductSpuPageQueryResult;
import org.apache.ibatis.annotations.Param;

/**
 * 类名: ProductSpuMapper
 * 描述: 商品SPU数据访问层
 * 作者: xuzeyu
 * 创建时间: 2026/6/25
 */
public interface ProductSpuMapper extends BaseMapperX<ProductSpu> {

    /**
     * 分页查询商品SPU列表，返回供应商、分类、品牌名称
     */
    IPage<ProductSpuPageQueryResult> selectPageWithName(Page<ProductSpuPageQueryResult> page,
                                                        @Param("command") ProductSpuPageQueryCommand command);

    /**
     * 商品详情
     */
    ProductDetails selectDetailsWithId(@Param("command") ProductDetailsCommand command);
}
