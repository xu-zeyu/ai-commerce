package com.jinHan.shop.core.supplier.domain.handler;

import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jinHan.shop.core.supplier.domain.command.SupplierUpdateCommand;
import com.jinHan.shop.core.supplier.domain.mapper.SupplierMapper;
import com.jinHan.shop.core.supplier.domain.model.Supplier;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 类名: SupplierUpdateHandler
 * 描述: 更新供应商处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/24
 */
@Component
public class SupplierUpdateHandler {

    @Resource
    private SupplierMapper supplierMapper;

    public Supplier update(SupplierUpdateCommand command) {
        Supplier existing = supplierMapper.selectById(command.getId());
        if (existing == null) {
            throw new BusinessException("供应商不存在");
        }

        if (command.getSupplierCode() != null
                && !command.getSupplierCode().equals(existing.getSupplierCode())) {
            Long count = supplierMapper.selectCount(new LambdaQueryWrapper<Supplier>()
                    .eq(Supplier::getSupplierCode, command.getSupplierCode())
                    .ne(Supplier::getId, command.getId()));
            if (count != null && count > 0) {
                throw new BusinessException("供应商编码已存在");
            }
        }

        LambdaUpdateWrapper<Supplier> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Supplier::getId, command.getId());

        updateWrapper
                .set(command.getSupplierCode() != null, Supplier::getSupplierCode, command.getSupplierCode())
                .set(command.getSupplierName() != null, Supplier::getSupplierName, command.getSupplierName())
                .set(command.getContactName() != null, Supplier::getContactName, command.getContactName())
                .set(command.getContactPhone() != null, Supplier::getContactPhone, command.getContactPhone())
                .set(command.getEmail() != null, Supplier::getEmail, command.getEmail())
                .set(command.getAddress() != null, Supplier::getAddress, command.getAddress())
                .set(command.getStatus() != null, Supplier::getStatus, command.getStatus())
                .set(command.getRemark() != null, Supplier::getRemark, command.getRemark())
                .set(Supplier::getUpdatedTime, LocalDateTime.now());

        int result = supplierMapper.update(null, updateWrapper);
        if (result <= 0) {
            throw new BusinessException("更新供应商失败，可能记录不存在");
        }
        return supplierMapper.selectById(command.getId());
    }
}
