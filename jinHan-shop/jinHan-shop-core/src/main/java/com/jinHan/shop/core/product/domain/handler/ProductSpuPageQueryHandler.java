package com.jinHan.shop.core.product.domain.handler;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinHan.shop.core.product.domain.command.ProductSpuPageQueryCommand;
import com.jinHan.shop.core.product.domain.mapper.ProductSpuMapper;
import com.jinHan.shop.core.product.domain.model.ProductSpuPageQueryResult;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

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
    public IPage<ProductSpuPageQueryResult> queryPage(ProductSpuPageQueryCommand command) {
        return productSpuMapper.selectPageWithName(new Page<>(command.getPage(), command.getSize()), command);
    }
}
