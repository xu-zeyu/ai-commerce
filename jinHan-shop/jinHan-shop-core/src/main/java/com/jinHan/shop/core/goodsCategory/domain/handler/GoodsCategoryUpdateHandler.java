package com.jinHan.shop.core.goodsCategory.domain.handler;

import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jinHan.shop.core.goodsCategory.domain.command.GoodsCategoryUpdateCommand;
import com.jinHan.shop.core.goodsCategory.domain.mapper.GoodsCategoryMapper;
import com.jinHan.shop.core.goodsCategory.domain.model.GoodsCategory;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 类名: GoodsCategoryUpdateHandler
 * 描述: 更新商品分类处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/22
 */
@Component
public class GoodsCategoryUpdateHandler {
    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;

    public GoodsCategory update(GoodsCategoryUpdateCommand command) {
        try {
            // 构建更新条件
            LambdaUpdateWrapper<GoodsCategory> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(GoodsCategory::getId, command.getId())
                    .set(command.getParent_id() != null, GoodsCategory::getParentId, command.getParent_id())
                    .set(command.getName() != null, GoodsCategory::getName, command.getName())
                    .set(command.getIcon() != null, GoodsCategory::getIcon, command.getIcon())
                    .set(command.getLevel() != null, GoodsCategory::getLevel, command.getLevel())
                    .set(command.getSort() != null, GoodsCategory::getSort, command.getSort())
                    .set(GoodsCategory::getStatus, command.getStatus())
                    .set(GoodsCategory::getUpdatedTime, LocalDateTime.now());

            // 执行更新
            int result = goodsCategoryMapper.update(null, updateWrapper);
            if (result <= 0) {
                throw new BusinessException("更新商品失败，可能记录不存在");
            }
            return goodsCategoryMapper.selectById(command.getId());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

