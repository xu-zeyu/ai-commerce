package com.jinHan.shop.core.supplier.domain.handler;

import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jinHan.shop.core.supplier.domain.command.SupplierBrandUpdateCommand;
import com.jinHan.shop.core.supplier.domain.mapper.SupplierBrandMapper;
import com.jinHan.shop.core.supplier.domain.model.SupplierBrand;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: SupplierBrandUpdateHandler
 * 描述: 更新供应商品牌关系处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/24
 */
@Component
public class SupplierBrandUpdateHandler {

    @Resource
    private SupplierBrandMapper supplierBrandMapper;

    public SupplierBrand update(SupplierBrandUpdateCommand command) {
        SupplierBrand existing = supplierBrandMapper.selectById(command.getId());
        if (existing == null) {
            throw new BusinessException("供应商品牌关系不存在");
        }

        if (command.getSupplierId() != null && command.getBrandId() != null
                && (!command.getSupplierId().equals(existing.getSupplierId())
                || !command.getBrandId().equals(existing.getBrandId()))) {
            Long count = supplierBrandMapper.selectCount(new LambdaQueryWrapper<SupplierBrand>()
                    .eq(SupplierBrand::getSupplierId, command.getSupplierId())
                    .eq(SupplierBrand::getBrandId, command.getBrandId())
                    .ne(SupplierBrand::getId, command.getId()));
            if (count != null && count > 0) {
                throw new BusinessException("该供应商已关联此品牌");
            }
        }

        LambdaUpdateWrapper<SupplierBrand> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SupplierBrand::getId, command.getId());

        updateWrapper
                .set(command.getSupplierId() != null, SupplierBrand::getSupplierId, command.getSupplierId())
                .set(command.getBrandId() != null, SupplierBrand::getBrandId, command.getBrandId());

        int result = supplierBrandMapper.update(null, updateWrapper);
        if (result <= 0) {
            throw new BusinessException("更新供应商品牌关系失败，可能记录不存在");
        }
        return supplierBrandMapper.selectById(command.getId());
    }
}
