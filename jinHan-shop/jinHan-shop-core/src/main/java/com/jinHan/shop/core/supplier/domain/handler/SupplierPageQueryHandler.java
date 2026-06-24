package com.jinHan.shop.core.supplier.domain.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinHan.shop.core.supplier.domain.command.SupplierPageQueryCommand;
import com.jinHan.shop.core.supplier.domain.mapper.SupplierMapper;
import com.jinHan.shop.core.supplier.domain.model.Supplier;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 类名: SupplierPageQueryHandler
 * 描述: 分页查询供应商列表处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/24
 */
@Component
public class SupplierPageQueryHandler {

    @Resource
    private SupplierMapper supplierMapper;

    public IPage<Supplier> queryPage(SupplierPageQueryCommand command) {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<Supplier>()
                .like(StringUtils.hasText(command.getSupplierCode()), Supplier::getSupplierCode, command.getSupplierCode())
                .like(StringUtils.hasText(command.getSupplierName()), Supplier::getSupplierName, command.getSupplierName())
                .eq(command.getStatus() != null, Supplier::getStatus, command.getStatus())
                .orderByDesc(Supplier::getCreatedTime);

        return supplierMapper.selectPage(new Page<>(command.getPage(), command.getSize()), wrapper);
    }
}
