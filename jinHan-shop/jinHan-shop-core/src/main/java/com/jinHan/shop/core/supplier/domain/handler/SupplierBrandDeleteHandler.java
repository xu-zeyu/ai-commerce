package com.jinHan.shop.core.supplier.domain.handler;

import com.aicommerce.common.exception.BusinessException;
import com.jinHan.shop.core.supplier.domain.mapper.SupplierBrandMapper;
import com.jinHan.shop.core.supplier.domain.model.SupplierBrand;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: SupplierBrandDeleteHandler
 * 描述: 删除供应商品牌关系处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/24
 */
@Component
public class SupplierBrandDeleteHandler {

    @Resource
    private SupplierBrandMapper supplierBrandMapper;

    public void delete(Long id) {
        SupplierBrand supplierBrand = supplierBrandMapper.selectById(id);
        if (supplierBrand == null) {
            throw new BusinessException("供应商品牌关系不存在");
        }
        int result = supplierBrandMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除供应商品牌关系失败");
        }
    }
}
