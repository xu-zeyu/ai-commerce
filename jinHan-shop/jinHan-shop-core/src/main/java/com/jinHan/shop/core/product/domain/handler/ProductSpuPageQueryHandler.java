package com.jinHan.shop.core.product.domain.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinHan.shop.core.product.domain.command.ProductSpuPageQueryCommand;
import com.jinHan.shop.core.product.domain.mapper.ProductSpuMapper;
import com.jinHan.shop.core.product.domain.model.AuditStatusEnum;
import com.jinHan.shop.core.product.domain.model.ProductSpu;
import com.jinHan.shop.core.product.domain.model.SaleStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * 类名: ProductSpuPageQueryHandler
 * 描述: 分页查询商品SPU列表处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/26
 */
@Component
public class ProductSpuPageQueryHandler {

    @Resource
    private ProductSpuMapper productSpuMapper;

    /**
     * 分页查询商品SPU列表，支持按名称/编码模糊搜索，以及供应商、分类、品牌、销售状态、审核状态筛选
     */
    public IPage<ProductSpu> queryPage(ProductSpuPageQueryCommand command) {
        SaleStatusEnum saleStatus = parseSaleStatus(command.getSaleStatus());
        AuditStatusEnum auditStatus = parseAuditStatus(command.getAuditStatus());

        LambdaQueryWrapper<ProductSpu> wrapper = new LambdaQueryWrapper<ProductSpu>()
                .like(StringUtils.hasText(command.getName()), ProductSpu::getName, command.getName())
                .like(StringUtils.hasText(command.getSpuCode()), ProductSpu::getSpuCode, command.getSpuCode())
                .eq(command.getSupplierId() != null, ProductSpu::getSupplierId, command.getSupplierId())
                .eq(command.getCategoryId() != null, ProductSpu::getCategoryId, command.getCategoryId())
                .eq(command.getBrandId() != null, ProductSpu::getBrandId, command.getBrandId())
                .eq(saleStatus != null, ProductSpu::getSaleStatus, saleStatus)
                .eq(auditStatus != null, ProductSpu::getAuditStatus, auditStatus)
                .orderByAsc(ProductSpu::getSort)
                .orderByDesc(ProductSpu::getCreatedTime);

        return productSpuMapper.selectPage(new Page<>(command.getPage(), command.getSize()), wrapper);
    }

    private SaleStatusEnum parseSaleStatus(Integer code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(SaleStatusEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    private AuditStatusEnum parseAuditStatus(Integer code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(AuditStatusEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

}
