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
        // 计算分类层级：一级分类 parentId=0，子分类根据父级推导
        int level;
        Long parentId = command.getParent_id();
        if (parentId == null || parentId == 0) {
            level = 1;
        } else {
            GoodsCategory parent = goodsCategoryMapper.selectById(parentId);
            if (parent == null) {
                throw new BusinessException("父级分类不存在");
            }
            if (parent.getLevel() >= GoodsCategory.MAX_LEVEL) {
                throw new BusinessException("商品分类最多支持三级，无法继续添加子分类");
            }
            level = parent.getLevel() + 1;
        }

        GoodsCategory goodsCategory = new GoodsCategory();
        goodsCategory.setParentId(parentId);
        goodsCategory.setName(command.getName());
        goodsCategory.setIcon(command.getIcon());
        goodsCategory.setLevel(level);
        goodsCategory.setSort(command.getSort());
        goodsCategory.setStatus(command.getStatus());
        int inserted = goodsCategoryMapper.insert(goodsCategory);
        if (inserted <= 0) {
            throw new BusinessException("创建商品分类失败");
        }

        return goodsCategory.getId();
    }
}
