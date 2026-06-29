package com.jinHan.shop.core.product.domain.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jinHan.shop.core.admin.domain.mapper.AdminMapper;
import com.jinHan.shop.core.admin.domain.model.Admin;
import com.jinHan.shop.core.goodsBrand.domain.model.GoodsBrand;
import com.jinHan.shop.core.product.domain.command.ProductSpuCommand;
import com.jinHan.shop.core.product.domain.mapper.ProductSpuMapper;
import com.jinHan.shop.core.product.domain.model.ProductSpu;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 类名: ProductSpuCommand
 * 描述: 编辑商品指令
 * 作者: xuzeyu
 * 创建时间: 2026/6/29
 */

@Component
public class ProductSpuUpdateHandler {
    @Resource
    private ProductSpuMapper productSpuMapper;

    public ProductSpu update (ProductSpuCommand command, Long id) {
        // 构建更新条件
        LambdaUpdateWrapper<ProductSpu> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProductSpu::getId, id);

        updateWrapper
                .set(command.getName() != null, ProductSpu::getName, command.getName())
                .set(command.getSupplierId() != null, ProductSpu::getSupplierId, command.getSupplierId())
                .set(command.getCategoryId() != null, ProductSpu::getCategoryId, command.getCategoryId())
                .set(command.getSort() != null, ProductSpu::getSort, command.getSort())
                .set(command.getSpuCode() != null, ProductSpu::getSpuCode, command.getSpuCode())
                .set(command.getBrandId() != null, ProductSpu::getBrandId, command.getBrandId())
                .set(command.getSalesCount() != null, ProductSpu::getSalesCount, command.getSalesCount())
                .set(command.getSaleStatus() != null, ProductSpu::getSaleStatus, command.getSaleStatus())
                .set(command.getSubTitle() != null, ProductSpu::getSubTitle, command.getSubTitle())
                .set(ProductSpu::getUpdatedTime, LocalDateTime.now())
                .set(ProductSpu::getUpdatedBy, StpUtil.getLoginId());


        int result = productSpuMapper.update(null, updateWrapper);
        if (result <= 0) {
            throw new BusinessException("更新商品spu失败，可能记录不存在");
        }

        return productSpuMapper.selectById(id);
    }
}
