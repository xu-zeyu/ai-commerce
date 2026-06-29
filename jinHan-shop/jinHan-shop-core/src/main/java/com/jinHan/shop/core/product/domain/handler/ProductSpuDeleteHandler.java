package com.jinHan.shop.core.product.domain.handler;

import com.jinHan.shop.core.product.domain.mapper.ProductSpuMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: ProductSpuDeleteHandler
 * 描述: 商品SPU删除处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/29
 */
@Component
public class ProductSpuDeleteHandler {

    @Resource
    private ProductSpuMapper productSpuMapper;

    public void delete(Long id) {
        productSpuMapper.deleteById(id);
    }
}
