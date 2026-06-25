package com.jinHan.shop.core.supplier.domain.handler;

import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jinHan.shop.core.supplier.domain.command.SupplierBrandCreateCommand;
import com.jinHan.shop.core.supplier.domain.mapper.SupplierBrandMapper;
import com.jinHan.shop.core.supplier.domain.model.SupplierBrand;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: SupplierBrandCreateHandler
 * 描述: 新增供应商品牌关系处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/24
 */
@Component
public class SupplierBrandCreateHandler {

    @Resource
    private SupplierBrandMapper supplierBrandMapper;

    public Long create(SupplierBrandCreateCommand command) {
        Long count = supplierBrandMapper.selectCount(new LambdaQueryWrapper<SupplierBrand>()
                .eq(SupplierBrand::getSupplierId, command.getSupplierId())
                .eq(SupplierBrand::getBrandId, command.getBrandId()));
        if (count != null && count > 0) {
            throw new BusinessException("该供应商已关联此品牌");
        }

        SupplierBrand supplierBrand = new SupplierBrand();
        supplierBrand.setSupplierId(command.getSupplierId());
        supplierBrand.setBrandId(command.getBrandId());

        int inserted = supplierBrandMapper.insert(supplierBrand);
        if (inserted <= 0) {
            throw new BusinessException("创建供应商品牌关系失败");
        }
        return supplierBrand.getId();
    }
}
