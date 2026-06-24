package com.jinHan.shop.core.supplier.domain.handler;

import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jinHan.shop.core.supplier.domain.command.SupplierCreateCommand;
import com.jinHan.shop.core.supplier.domain.mapper.SupplierMapper;
import com.jinHan.shop.core.supplier.domain.model.Supplier;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: SupplierCreateHandler
 * 描述: 新增供应商处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/24
 */
@Component
public class SupplierCreateHandler {

    @Resource
    private SupplierMapper supplierMapper;

    public Long create(SupplierCreateCommand command) {
        Long count = supplierMapper.selectCount(new LambdaQueryWrapper<Supplier>()
                .eq(Supplier::getSupplierCode, command.getSupplierCode()));
        if (count != null && count > 0) {
            throw new BusinessException("供应商编码已存在");
        }

        Supplier supplier = new Supplier();
        supplier.setSupplierCode(command.getSupplierCode());
        supplier.setSupplierName(command.getSupplierName());
        supplier.setContactName(command.getContactName());
        supplier.setContactPhone(command.getContactPhone());
        supplier.setEmail(command.getEmail());
        supplier.setAddress(command.getAddress());
        supplier.setStatus(command.getStatus());
        supplier.setRemark(command.getRemark());

        int inserted = supplierMapper.insert(supplier);
        if (inserted <= 0) {
            throw new BusinessException("创建供应商失败");
        }
        return supplier.getId();
    }
}
