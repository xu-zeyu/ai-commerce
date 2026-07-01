package com.jinHan.shop.core.product.domain.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jinHan.shop.core.goodsBrand.domain.mapper.GoodsBrandMapper;
import com.jinHan.shop.core.goodsBrand.domain.model.GoodsBrand;
import com.jinHan.shop.core.goodsCategory.domain.mapper.GoodsCategoryMapper;
import com.jinHan.shop.core.goodsCategory.domain.model.GoodsCategory;
import com.jinHan.shop.core.product.domain.command.ProductSpuCommand;
import com.jinHan.shop.core.product.domain.mapper.ProductSpuMapper;
import com.jinHan.shop.core.product.domain.model.AuditStatusEnum;
import com.jinHan.shop.core.product.domain.model.ProductSpu;
import com.jinHan.shop.core.supplier.domain.mapper.SupplierMapper;
import com.jinHan.shop.core.supplier.domain.model.Supplier;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

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

    @Resource
    private SupplierMapper supplierMapper;

    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;

    @Resource
    private GoodsBrandMapper goodsBrandMapper;

    public Long create(ProductSpuCommand command) {
        // 校验 SPU 编码唯一性
        Long count = productSpuMapper.selectCount(new LambdaQueryWrapper<ProductSpu>()
                .eq(ProductSpu::getSpuCode, command.getSpuCode()));
        if (count != null && count > 0) {
            throw new BusinessException("SPU编码已存在");
        }

        // 校验供应商是否存在
        Supplier supplier = supplierMapper.selectById(command.getSupplierId());
        if (supplier == null) {
            throw new BusinessException("供应商不存在");
        }

        // 校验分类是否存在
        GoodsCategory category = goodsCategoryMapper.selectById(command.getCategoryId());
        if (category == null) {
            throw new BusinessException("商品分类不存在");
        }

        // 校验品牌是否存在
        GoodsBrand brand = goodsBrandMapper.selectById(command.getBrandId());
        if (brand == null) {
            throw new BusinessException("商品品牌不存在");
        }

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
        productSpu.setSaleStatus(command.getSaleStatus());
        productSpu.setAuditStatus(auditStatusEnum);
        productSpu.setSort(command.getSort());
        productSpu.setCreatedBy(Long.valueOf(StpUtil.getLoginId().toString()));

        // 入库
        int inserted = productSpuMapper.insert(productSpu);
        if (inserted <= 0) {
            throw new BusinessException("创建商品SPU失败");
        }
        return productSpu.getId();
    }
}
