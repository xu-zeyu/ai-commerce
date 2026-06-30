package com.jinHan.shop.core.product.domain.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jinHan.shop.core.product.domain.command.ProductSpuImageCommand;
import com.jinHan.shop.core.product.domain.mapper.ProductSpuImageMapper;
import com.jinHan.shop.core.product.domain.mapper.ProductSpuMapper;
import com.jinHan.shop.core.product.domain.model.ProductSpuImage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: ProductSpuImageCreateHandler
 * 描述: 新增商品SPU图片处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/30
 */
@Component
public class ProductSpuImageCreateHandler {

    @Resource
    private ProductSpuImageMapper productSpuImageMapper;

    @Resource
    private ProductSpuMapper productSpuMapper;

    public Long create(ProductSpuImageCommand command) {
        // 校验所属 SPU 存在
        if (productSpuMapper.selectById(command.getSpuId()) == null) {
            throw new BusinessException("商品SPU不存在");
        }

        // 校验同一 SPU 下图片类型唯一
        Long count = productSpuImageMapper.selectCount(new LambdaQueryWrapper<ProductSpuImage>()
                .eq(ProductSpuImage::getSpuId, command.getSpuId())
                .eq(ProductSpuImage::getImageType, command.getImageType()));
        if (count != null && count > 0) {
            throw new BusinessException("该SPU下此图片类型已存在");
        }

        // 组装实体
        ProductSpuImage productSpuImage = new ProductSpuImage();
        productSpuImage.setSpuId(command.getSpuId());
        productSpuImage.setImageType(command.getImageType());
        productSpuImage.setImageUrls(command.getImageUrls());
        productSpuImage.setCreatedBy(Long.valueOf(StpUtil.getLoginId().toString()));

        // 入库
        int inserted = productSpuImageMapper.insert(productSpuImage);
        if (inserted <= 0) {
            throw new BusinessException("创建商品SPU图片失败");
        }
        return productSpuImage.getId();
    }
}
