package com.jinHan.shop.core.product.domain.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jinHan.shop.core.product.domain.command.ProductSkuCommand;
import com.jinHan.shop.core.product.domain.mapper.ProductSkuMapper;
import com.jinHan.shop.core.product.domain.mapper.ProductSpuMapper;
import com.jinHan.shop.core.product.domain.model.ProductSku;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 类名: ProductSkuUpdateHandler
 * 描述: 编辑商品SKU处理器
 * 作者: xuzeyu
 * 创建时间: 2026/7/1
 */
@Component
public class ProductSkuUpdateHandler {

    @Resource
    private ProductSkuMapper productSkuMapper;

    @Resource
    private ProductSpuMapper productSpuMapper;

    public ProductSku update(ProductSkuCommand command, Long id) {
        // 校验记录存在
        ProductSku existing = productSkuMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("商品SKU不存在");
        }

        // 校验所属 SPU 存在（若修改了归属）
        if (!existing.getSpuId().equals(command.getSpuId())) {
            if (productSpuMapper.selectById(command.getSpuId()) == null) {
                throw new BusinessException("商品SPU不存在");
            }
        }

        // 校验 SKU 编码唯一性（若修改了编码）
        if (!existing.getSkuCode().equals(command.getSkuCode())) {
            Long count = productSkuMapper.selectCount(new LambdaQueryWrapper<ProductSku>()
                    .eq(ProductSku::getSkuCode, command.getSkuCode()));
            if (count != null && count > 0) {
                throw new BusinessException("SKU编码已存在");
            }
        }

        // 构建更新条件
        LambdaUpdateWrapper<ProductSku> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProductSku::getId, id)
                .set(ProductSku::getSpuId, command.getSpuId())
                .set(ProductSku::getSkuCode, command.getSkuCode())
                .set(ProductSku::getPrice, command.getPrice())
                .set(ProductSku::getStock, command.getStock())
                .set(command.getSpecInfo() != null, ProductSku::getSpecInfo, command.getSpecInfo())
                .set(command.getImage() != null, ProductSku::getImage, command.getImage())
                .set(command.getOriginalPrice() != null, ProductSku::getOriginalPrice, command.getOriginalPrice())
                .set(command.getStatus() != null, ProductSku::getStatus, command.getStatus())
                .set(ProductSku::getUpdatedTime, LocalDateTime.now())
                .set(ProductSku::getUpdatedBy, Long.valueOf(StpUtil.getLoginId().toString()));

        int result = productSkuMapper.update(null, updateWrapper);
        if (result <= 0) {
            throw new BusinessException("更新商品SKU失败");
        }

        return productSkuMapper.selectById(id);
    }
}
