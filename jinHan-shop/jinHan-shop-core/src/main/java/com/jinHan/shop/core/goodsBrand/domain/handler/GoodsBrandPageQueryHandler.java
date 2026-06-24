package com.jinHan.shop.core.goodsBrand.domain.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinHan.shop.core.goodsBrand.domain.command.GoodsBrandPageQueryCommand;
import com.jinHan.shop.core.goodsBrand.domain.mapper.GoodsBrandMapper;
import com.jinHan.shop.core.goodsBrand.domain.model.GoodsBrand;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 类名: GoodsBrandPageQueryHandler
 * 描述: 分页查询商品品牌列表处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/23
 */
@Component
public class GoodsBrandPageQueryHandler {

    @Resource
    private GoodsBrandMapper goodsBrandMapper;

    /**
     * 分页查询商品品牌列表，支持按名称模糊搜索和状态筛选
     */
    public IPage<GoodsBrand> queryPage(GoodsBrandPageQueryCommand command) {
        LambdaQueryWrapper<GoodsBrand> wrapper = new LambdaQueryWrapper<GoodsBrand>()
                .like(StringUtils.hasText(command.getName()), GoodsBrand::getName, command.getName())
                .eq(command.getCategoryId() != null, GoodsBrand::getCategoryId, command.getCategoryId())
                .eq(command.getStatus() != null, GoodsBrand::getStatus, command.getStatus())
                .orderByAsc(GoodsBrand::getSort)
                .orderByDesc(GoodsBrand::getCreatedTime);

        return goodsBrandMapper.selectPage(new Page<>(command.getPage(), command.getSize()), wrapper);
    }

}
