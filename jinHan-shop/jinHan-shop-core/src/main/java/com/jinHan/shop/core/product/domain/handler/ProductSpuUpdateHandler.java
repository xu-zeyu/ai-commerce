package com.jinHan.shop.core.product.domain.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jinHan.shop.core.product.domain.command.ProductSpuCommand;
import com.jinHan.shop.core.product.domain.mapper.ProductSpuMapper;
import com.jinHan.shop.core.product.domain.model.ProductSpu;
import com.jinHan.shop.core.product.domain.model.SaleStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 类名: ProductSpuUpdateHandler
 * 描述: 编辑商品SPU处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/29
 */
@Component
public class ProductSpuUpdateHandler {
    @Resource
    private ProductSpuMapper productSpuMapper;

    public ProductSpu update(ProductSpuCommand command, Long id) {
        // 校验记录存在
        ProductSpu existing = productSpuMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("商品SPU不存在");
        }

        // 校验 SPU 编码唯一性（若修改了编码）
        if (!existing.getSpuCode().equals(command.getSpuCode())) {
            Long count = productSpuMapper.selectCount(new LambdaQueryWrapper<ProductSpu>()
                    .eq(ProductSpu::getSpuCode, command.getSpuCode()));
            if (count != null && count > 0) {
                throw new BusinessException("SPU编码已存在");
            }
        }

        // 构建更新条件
        LambdaUpdateWrapper<ProductSpu> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProductSpu::getId, id)
                .set(ProductSpu::getName, command.getName())
                .set(ProductSpu::getSupplierId, command.getSupplierId())
                .set(ProductSpu::getCategoryId, command.getCategoryId())
                .set(ProductSpu::getSpuCode, command.getSpuCode())
                .set(ProductSpu::getBrandId, command.getBrandId())
                .set(ProductSpu::getSaleStatus, command.getSaleStatus())
                .set(command.getSubTitle() != null, ProductSpu::getSubTitle, command.getSubTitle())
                .set(command.getSort() != null, ProductSpu::getSort, command.getSort())
                .set(ProductSpu::getUpdatedTime, LocalDateTime.now())
                .set(ProductSpu::getUpdatedBy, Long.valueOf(StpUtil.getLoginId().toString()));

        int result = productSpuMapper.update(null, updateWrapper);
        if (result <= 0) {
            throw new BusinessException("更新商品SPU失败");
        }

        return productSpuMapper.selectById(id);
    }
}
