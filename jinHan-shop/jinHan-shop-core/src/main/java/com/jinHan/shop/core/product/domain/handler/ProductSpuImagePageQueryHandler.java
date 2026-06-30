package com.jinHan.shop.core.product.domain.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jinHan.shop.core.product.domain.command.ProductSpuImagePageQueryCommand;
import com.jinHan.shop.core.product.domain.mapper.ProductSpuImageMapper;
import com.jinHan.shop.core.product.domain.model.ProductSpuImage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: ProductSpuImagePageQueryHandler
 * 描述: 分页查询商品SPU图片列表处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/30
 */
@Component
public class ProductSpuImagePageQueryHandler {

    @Resource
    private ProductSpuImageMapper productSpuImageMapper;

    /**
     * 分页查询商品SPU图片列表，支持按 SPU、图片类型筛选
     */
    public IPage<ProductSpuImage> queryPage(ProductSpuImagePageQueryCommand command) {
        LambdaQueryWrapper<ProductSpuImage> queryWrapper = new LambdaQueryWrapper<ProductSpuImage>()
                .eq(command.getSpuId() != null, ProductSpuImage::getSpuId, command.getSpuId())
                .eq(command.getImageType() != null, ProductSpuImage::getImageType, command.getImageType())
                .orderByDesc(ProductSpuImage::getCreatedTime);
        return productSpuImageMapper.selectPage(command, queryWrapper);
    }
}
