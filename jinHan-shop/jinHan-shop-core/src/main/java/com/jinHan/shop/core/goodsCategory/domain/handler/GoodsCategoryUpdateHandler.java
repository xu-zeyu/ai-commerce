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
            updateWrapper.eq(GoodsCategory::getId, command.getId());

            // 如果修改了父级分类，校验层级不超过最大限制
            Long parentId = command.getParent_id();
            if (parentId != null && parentId != 0) {
                GoodsCategory parent = goodsCategoryMapper.selectById(parentId);
                if (parent == null) {
                    throw new BusinessException("父级分类不存在");
                }
                if (parent.getLevel() >= GoodsCategory.MAX_LEVEL) {
                    throw new BusinessException("商品分类最多支持三级，无法继续添加子分类");
                }
                updateWrapper.set(GoodsCategory::getParentId, parentId);
                updateWrapper.set(GoodsCategory::getLevel, parent.getLevel() + 1);
            } else if (parentId != null && parentId == 0) {
                updateWrapper.set(GoodsCategory::getParentId, 0);
                updateWrapper.set(GoodsCategory::getLevel, 1);
            }

            updateWrapper
                    .set(command.getName() != null, GoodsCategory::getName, command.getName())
                    .set(command.getIcon() != null, GoodsCategory::getIcon, command.getIcon())
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

