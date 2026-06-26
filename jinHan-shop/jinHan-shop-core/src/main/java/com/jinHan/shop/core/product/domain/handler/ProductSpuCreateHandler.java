package com.jinHan.shop.core.product.domain.handler;

import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jinHan.shop.core.product.domain.command.ProductSpuCreateCommand;
import com.jinHan.shop.core.product.domain.mapper.ProductSpuMapper;
import com.jinHan.shop.core.product.domain.model.AuditStatusEnum;
import com.jinHan.shop.core.product.domain.model.ProductSpu;
import com.jinHan.shop.core.product.domain.model.SaleStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 类名: ProductSpuCreateHandler
 * 描述: 新增商品SPU处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/25
 */
@Component
public class ProductSpuCreateHandler {

    @Resource
    private ProductSpuMapper productSpuMapper;

    public Long create(ProductSpuCreateCommand command) {
        // 校验 SPU 编码唯一性
        Long count = productSpuMapper.selectCount(new LambdaQueryWrapper<ProductSpu>()
                .eq(ProductSpu::getSpuCode, command.getSpuCode()));
        if (count != null && count > 0) {
            throw new BusinessException("SPU编码已存在");
        }

        // 校验销售状态
        SaleStatusEnum saleStatusEnum = Arrays.stream(SaleStatusEnum.values())
                .filter(e -> e.getCode().equals(command.getSaleStatus()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("无效的销售状态"));

        // 审核状态默认为待审核
        AuditStatusEnum auditStatusEnum = AuditStatusEnum.PENDING;

        // 组装实体
        ProductSpu productSpu = new ProductSpu();
        productSpu.setSupplierId(command.getSupplierId());
        productSpu.setSpuCode(command.getSpuCode());
        productSpu.setName(command.getName());
        productSpu.setSubTitle(command.getSubTitle());
        productSpu.setCategoryId(command.getCategoryId());
        productSpu.setBrandId(command.getBrandId());
        productSpu.setSaleStatus(saleStatusEnum);
        productSpu.setAuditStatus(auditStatusEnum);
        productSpu.setSort(command.getSort());
        productSpu.setSalesCount(command.getSalesCount());

        // 入库
        int inserted = productSpuMapper.insert(productSpu);
        if (inserted <= 0) {
            throw new BusinessException("创建商品SPU失败");
        }
        return productSpu.getId();
    }
}
