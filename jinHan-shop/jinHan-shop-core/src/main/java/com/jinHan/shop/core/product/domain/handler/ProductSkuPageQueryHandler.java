package com.jinHan.shop.core.product.domain.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jinHan.shop.core.product.domain.command.ProductSkuPageQueryCommand;
import com.jinHan.shop.core.product.domain.mapper.ProductSkuMapper;
import com.jinHan.shop.core.product.domain.model.ProductSku;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: ProductSkuPageQueryHandler
 * 描述: 分页查询商品SKU列表处理器
 * 作者: xuzeyu
 * 创建时间: 2026/7/1
 */
@Component
public class ProductSkuPageQueryHandler {

    @Resource
    private ProductSkuMapper productSkuMapper;

    /**
     * 分页查询商品SKU列表，支持按 SPU ID、SKU编码、状态筛选
     */
    public IPage<ProductSku> queryPage(ProductSkuPageQueryCommand command) {
        LambdaQueryWrapper<ProductSku> queryWrapper = new LambdaQueryWrapper<ProductSku>()
                .eq(command.getSpuId() != null, ProductSku::getSpuId, command.getSpuId())
                .like(command.getSkuCode() != null, ProductSku::getSkuCode, command.getSkuCode())
                .eq(command.getStatus() != null, ProductSku::getStatus, command.getStatus())
                .orderByDesc(ProductSku::getCreatedTime);
        return productSkuMapper.selectPage(command, queryWrapper);
    }
}
