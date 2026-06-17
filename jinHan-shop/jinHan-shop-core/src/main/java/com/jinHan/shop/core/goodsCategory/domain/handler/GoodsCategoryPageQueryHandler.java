package com.jinHan.shop.core.goodsCategory.domain.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinHan.shop.core.goodsCategory.domain.command.GoodsCategoryPageQueryCommand;
import com.jinHan.shop.core.goodsCategory.domain.mapper.GoodsCategoryMapper;
import com.jinHan.shop.core.goodsCategory.domain.model.GoodsCategory;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: GoodsCategoryPageQueryHandler
 * 描述: 分析查询商品分类列表处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/17
 */
@Component
public class GoodsCategoryPageQueryHandler {
    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;


    /*
    * 分页查询  父 id 为 0 则是一级分类列表
    * */
    public IPage<GoodsCategory> queryPage(GoodsCategoryPageQueryCommand command) {
        LambdaQueryWrapper<GoodsCategory> wrapper = new LambdaQueryWrapper<GoodsCategory>()
                .eq(GoodsCategory::getParentId, command.getParentId())
                .like(command.getName() != null, GoodsCategory::getName, command.getName())
                .orderByDesc(GoodsCategory::getCreatedTime);
        return goodsCategoryMapper.selectPage(new Page<>(command.getPage(), command.getSize()), wrapper);
    }

}
