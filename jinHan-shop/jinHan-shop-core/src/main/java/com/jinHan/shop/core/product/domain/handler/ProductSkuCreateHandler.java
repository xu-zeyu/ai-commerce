package com.jinHan.shop.core.product.domain.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jinHan.shop.core.product.domain.command.ProductSkuCommand;
import com.jinHan.shop.core.product.domain.mapper.ProductSkuMapper;
import com.jinHan.shop.core.product.domain.mapper.ProductSpuMapper;
import com.jinHan.shop.core.product.domain.model.ProductSku;
import com.jinHan.shop.core.product.domain.model.SkuStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: ProductSkuCreateHandler
 * 描述: 新增商品SKU处理器
 * 作者: xuzeyu
 * 创建时间: 2026/7/1
 */
@Component
public class ProductSkuCreateHandler {

    @Resource
    private ProductSkuMapper productSkuMapper;

    @Resource
    private ProductSpuMapper productSpuMapper;

    public Long create(ProductSkuCommand command) {
        // 校验所属 SPU 存在
        if (productSpuMapper.selectById(command.getSpuId()) == null) {
            throw new BusinessException("商品SPU不存在");
        }

        // 校验 SKU 编码唯一性
        Long count = productSkuMapper.selectCount(new LambdaQueryWrapper<ProductSku>()
                .eq(ProductSku::getSkuCode, command.getSkuCode()));
        if (count != null && count > 0) {
            throw new BusinessException("SKU编码已存在");
        }

        // 组装实体
        ProductSku productSku = new ProductSku();
        productSku.setSpuId(command.getSpuId());
        productSku.setSkuCode(command.getSkuCode());
        productSku.setSpecInfo(command.getSpecInfo());
        productSku.setImage(command.getImage());
        productSku.setPrice(command.getPrice());
        productSku.setOriginalPrice(command.getOriginalPrice());
        productSku.setStock(command.getStock());
        productSku.setSalesCount(0);
        productSku.setStatus(command.getStatus() != null ? command.getStatus() : SkuStatusEnum.ENABLED);
        productSku.setCreatedBy(Long.valueOf(StpUtil.getLoginId().toString()));

        // 入库
        int inserted = productSkuMapper.insert(productSku);
        if (inserted <= 0) {
            throw new BusinessException("创建商品SKU失败");
        }
        return productSku.getId();
    }
}
