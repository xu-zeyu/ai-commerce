package com.jinHan.shop.core.product.domain.handler;

import com.jinHan.shop.core.product.domain.command.ProductDetailsCommand;
import com.jinHan.shop.core.product.domain.mapper.ProductSpuMapper;
import com.jinHan.shop.core.product.domain.model.ProductDetails;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: ProductDetailsHandler
 * 描述: 商品详情处理器
 * 作者: xuzeyu
 * 创建时间: 2026/7/3
 */
@Component
public class ProductDetailsHandler {

    @Resource
    private ProductSpuMapper productSpuMapper;

    public ProductDetails details (ProductDetailsCommand command) {
        return productSpuMapper.selectDetailsWithId(command);
    }
}
