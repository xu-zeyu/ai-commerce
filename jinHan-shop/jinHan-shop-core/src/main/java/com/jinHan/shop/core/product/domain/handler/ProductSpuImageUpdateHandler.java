package com.jinHan.shop.core.product.domain.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jinHan.shop.core.product.domain.command.ProductSpuImageCommand;
import com.jinHan.shop.core.product.domain.mapper.ProductSpuImageMapper;
import com.jinHan.shop.core.product.domain.model.ProductSpuImage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: ProductSpuImageUpdateHandler
 * 描述: 编辑商品SPU图片处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/30
 */
@Component
public class ProductSpuImageUpdateHandler {

    @Resource
    private ProductSpuImageMapper productSpuImageMapper;

    public ProductSpuImage update(ProductSpuImageCommand command, Long id) {
        // 校验记录存在
        ProductSpuImage existing = productSpuImageMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("商品SPU图片不存在");
        }

        // 校验同一 SPU 下图片类型唯一（若修改了归属或类型）
        if (!existing.getSpuId().equals(command.getSpuId())
                || !existing.getImageType().equals(command.getImageType())) {
            Long count = productSpuImageMapper.selectCount(new LambdaQueryWrapper<ProductSpuImage>()
                    .eq(ProductSpuImage::getSpuId, command.getSpuId())
                    .eq(ProductSpuImage::getImageType, command.getImageType())
                    .ne(ProductSpuImage::getId, id));
            if (count != null && count > 0) {
                throw new BusinessException("该SPU下此图片类型已存在");
            }
        }

        // 构建更新条件
        LambdaUpdateWrapper<ProductSpuImage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProductSpuImage::getId, id)
                .set(ProductSpuImage::getSpuId, command.getSpuId())
                .set(ProductSpuImage::getImageType, command.getImageType())
                .set(ProductSpuImage::getImageUrls, command.getImageUrls())
                .set(ProductSpuImage::getUpdatedBy, Long.valueOf(StpUtil.getLoginId().toString()));

        int result = productSpuImageMapper.update(null, updateWrapper);
        if (result <= 0) {
            throw new BusinessException("更新商品SPU图片失败");
        }

        return productSpuImageMapper.selectById(id);
    }
}
