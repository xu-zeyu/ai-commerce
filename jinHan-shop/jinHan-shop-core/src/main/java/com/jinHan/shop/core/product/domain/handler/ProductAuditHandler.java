package com.jinHan.shop.core.product.domain.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jinHan.shop.core.product.domain.command.ProductDetailsCommand;
import com.jinHan.shop.core.product.domain.mapper.ProductSpuMapper;
import com.jinHan.shop.core.product.domain.model.AuditStatusEnum;
import com.jinHan.shop.core.product.domain.model.ProductDetails;
import com.jinHan.shop.core.product.domain.model.ProductSpu;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 类名: ProductAuditHandler
 * 描述: 商品审核处理器
 * 作者: xuzeyu
 * 创建时间: 2026/7/3
 */
@Component
public class ProductAuditHandler {

    @Resource
    private ProductSpuMapper productSpuMapper;

    public ProductDetails audit(Long id) {
        ProductSpu existing = productSpuMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("商品不存在");
        }
        if (existing.getAuditStatus() == AuditStatusEnum.PASS) {
            throw new BusinessException("商品已审核通过");
        }
        if (existing.getAuditStatus() == AuditStatusEnum.REJECT) {
            throw new BusinessException("商品已审核拒绝");
        }

        LambdaUpdateWrapper<ProductSpu> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProductSpu::getId, id)
                .set(ProductSpu::getAuditStatus, AuditStatusEnum.PASS)
                .set(ProductSpu::getUpdatedTime, LocalDateTime.now())
                .set(ProductSpu::getUpdatedBy, Long.valueOf(StpUtil.getLoginId().toString()));

        int result = productSpuMapper.update(null, updateWrapper);
        if (result <= 0) {
            throw new BusinessException("商品审核失败");
        }

        return productSpuMapper.selectDetailsWithId(new ProductDetailsCommand(id));
    }
}
