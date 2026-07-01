package com.jinHan.shop.core.product.domain.handler;

import com.aicommerce.common.exception.BusinessException;
import com.jinHan.shop.core.product.domain.mapper.ProductSkuMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: ProductSkuDeleteHandler
 * 描述: 商品SKU删除处理器
 * 作者: xuzeyu
 * 创建时间: 2026/7/1
 */
@Component
public class ProductSkuDeleteHandler {

    @Resource
    private ProductSkuMapper productSkuMapper;

    public void delete(Long id) {
        // 校验记录存在
        if (productSkuMapper.selectById(id) == null) {
            throw new BusinessException("商品SKU不存在");
        }

        // 逻辑删除SKU
        productSkuMapper.deleteById(id);
    }
}
