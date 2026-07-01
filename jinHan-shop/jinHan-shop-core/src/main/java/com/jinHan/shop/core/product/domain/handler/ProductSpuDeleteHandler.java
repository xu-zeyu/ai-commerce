package com.jinHan.shop.core.product.domain.handler;

import com.aicommerce.common.exception.BusinessException;
import com.jinHan.shop.core.product.domain.mapper.ProductSkuMapper;
import com.jinHan.shop.core.product.domain.mapper.ProductSpuImageMapper;
import com.jinHan.shop.core.product.domain.mapper.ProductSpuMapper;
import com.jinHan.shop.core.product.domain.model.ProductSku;
import com.jinHan.shop.core.product.domain.model.ProductSpuImage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @Resource
    private ProductSpuImageMapper productSpuImageMapper;

    @Resource
    private ProductSkuMapper productSkuMapper;

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 校验 SPU 存在
        if (productSpuMapper.selectById(id) == null) {
            throw new BusinessException("商品SPU不存在");
        }

        // 删除商品SPU（逻辑删除）
        productSpuMapper.deleteById(id);

        // 级联删除关联的商品图片（逻辑删除）
        productSpuImageMapper.delete(ProductSpuImage::getSpuId, id);

        // 级联删除关联的商品SKU（逻辑删除）
        productSkuMapper.delete(ProductSku::getSpuId, id);
    }
}
