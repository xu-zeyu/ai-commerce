package com.jinHan.shop.core.product.domain.handler;

import com.jinHan.shop.core.product.domain.mapper.ProductSpuImageMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: ProductSpuImageDeleteHandler
 * 描述: 商品SPU图片删除处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/30
 */
@Component
public class ProductSpuImageDeleteHandler {

    @Resource
    private ProductSpuImageMapper productSpuImageMapper;

    public void delete(Long id) {
        productSpuImageMapper.deleteById(id);
    }
}
