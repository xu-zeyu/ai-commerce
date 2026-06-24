package com.jinHan.shop.core.supplier.domain.handler;

import com.aicommerce.common.exception.BusinessException;
import com.jinHan.shop.core.supplier.domain.mapper.SupplierMapper;
import com.jinHan.shop.core.supplier.domain.model.Supplier;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: SupplierDeleteHandler
 * 描述: 删除供应商处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/24
 */
@Component
public class SupplierDeleteHandler {

    @Resource
    private SupplierMapper supplierMapper;

    public void delete(Long id) {
        Supplier supplier = supplierMapper.selectById(id);
        if (supplier == null) {
            throw new BusinessException("供应商不存在");
        }
        int result = supplierMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除供应商失败");
        }
    }
}
