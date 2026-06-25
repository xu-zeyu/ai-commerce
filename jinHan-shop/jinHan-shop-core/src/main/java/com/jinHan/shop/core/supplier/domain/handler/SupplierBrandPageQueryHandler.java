package com.jinHan.shop.core.supplier.domain.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinHan.shop.core.supplier.domain.command.SupplierBrandPageQueryCommand;
import com.jinHan.shop.core.supplier.domain.mapper.SupplierBrandMapper;
import com.jinHan.shop.core.supplier.domain.model.SupplierBrand;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: SupplierBrandPageQueryHandler
 * 描述: 分页查询供应商品牌关系列表处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/24
 */
@Component
public class SupplierBrandPageQueryHandler {

    @Resource
    private SupplierBrandMapper supplierBrandMapper;

    public IPage<SupplierBrand> queryPage(SupplierBrandPageQueryCommand command) {
        LambdaQueryWrapper<SupplierBrand> wrapper = new LambdaQueryWrapper<SupplierBrand>()
                .eq(command.getSupplierId() != null, SupplierBrand::getSupplierId, command.getSupplierId())
                .eq(command.getBrandId() != null, SupplierBrand::getBrandId, command.getBrandId())
                .orderByDesc(SupplierBrand::getCreatedTime);

        return supplierBrandMapper.selectPage(new Page<>(command.getPage(), command.getSize()), wrapper);
    }
}
