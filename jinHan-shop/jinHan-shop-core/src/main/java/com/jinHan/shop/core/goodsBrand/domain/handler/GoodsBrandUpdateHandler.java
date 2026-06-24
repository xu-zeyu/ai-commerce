package com.jinHan.shop.core.goodsBrand.domain.handler;

import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jinHan.shop.core.goodsBrand.domain.command.GoodsBrandUpdateCommand;
import com.jinHan.shop.core.goodsBrand.domain.mapper.GoodsBrandMapper;
import com.jinHan.shop.core.goodsBrand.domain.model.GoodsBrand;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 类名: GoodsBrandUpdateHandler
 * 描述: 更新商品品牌处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/23
 */
@Component
public class GoodsBrandUpdateHandler {

    @Resource
    private GoodsBrandMapper goodsBrandMapper;

    public GoodsBrand update(GoodsBrandUpdateCommand command) {
        // 构建更新条件
        LambdaUpdateWrapper<GoodsBrand> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(GoodsBrand::getId, command.getId());

        updateWrapper
                .set(command.getName() != null, GoodsBrand::getName, command.getName())
                .set(command.getLogo() != null, GoodsBrand::getLogo, command.getLogo())
                .set(command.getCategoryId() != null, GoodsBrand::getCategoryId, command.getCategoryId())
                .set(command.getSort() != null, GoodsBrand::getSort, command.getSort())
                .set(command.getStatus() != null, GoodsBrand::getStatus, command.getStatus())
                .set(GoodsBrand::getUpdatedTime, LocalDateTime.now());

        // 首字母：优先使用前端传值，否则根据名称自动提取
        String firstLetter = command.getFirstLetter();
        if (StringUtils.hasText(firstLetter)) {
            updateWrapper.set(GoodsBrand::getFirstLetter, firstLetter.toUpperCase());
        } else if (command.getName() != null) {
            updateWrapper.set(GoodsBrand::getFirstLetter, extractFirstLetter(command.getName()));
        }

        int result = goodsBrandMapper.update(null, updateWrapper);
        if (result <= 0) {
            throw new BusinessException("更新商品品牌失败，可能记录不存在");
        }

        return goodsBrandMapper.selectById(command.getId());
    }

    /**
     * 提取品牌名称首字母（大写）
     */
    private String extractFirstLetter(String name) {
        if (!StringUtils.hasText(name)) {
            return "#";
        }
        char firstChar = name.trim().charAt(0);
        if (Character.isLetter(firstChar)) {
            return String.valueOf(Character.toUpperCase(firstChar));
        }
        return "#";
    }

}
