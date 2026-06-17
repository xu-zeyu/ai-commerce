package com.jinHan.shop.core.goodsCategory.domain.handler;

import com.aicommerce.common.exception.BusinessException;
import com.jinHan.shop.core.goodsCategory.domain.command.GoodsCategoryCreateCommand;
import com.jinHan.shop.core.goodsCategory.domain.mapper.GoodsCategoryMapper;
import com.jinHan.shop.core.goodsCategory.domain.model.GoodsCategory;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: GoodsCategoryCreateHandler
 * 描述: 新增商品分类处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/15
 */
@Component
public class GoodsCategoryCreateHandler {

    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;

    public Long create(GoodsCategoryCreateCommand command) {
        GoodsCategory goodsCategory = new GoodsCategory();
        goodsCategory.setParentId(command.getParent_id());
        goodsCategory.setName(command.getName());
        goodsCategory.setIcon(command.getIcon());
        goodsCategory.setLevel(command.getLevel());
        goodsCategory.setSort(command.getSort());
        goodsCategory.setStatus(command.getStatus());
        int inserted = goodsCategoryMapper.insert(goodsCategory);
        if (inserted <= 0) {
            throw new BusinessException("创建商品分类失败");
        }

        return goodsCategory.getId();
    }
}
