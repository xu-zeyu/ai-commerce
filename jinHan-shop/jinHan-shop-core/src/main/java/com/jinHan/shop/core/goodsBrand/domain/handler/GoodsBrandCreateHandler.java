package com.jinHan.shop.core.goodsBrand.domain.handler;

import com.aicommerce.common.exception.BusinessException;
import com.jinHan.shop.core.goodsBrand.domain.command.GoodsBrandCreateCommand;
import com.jinHan.shop.core.goodsBrand.domain.mapper.GoodsBrandMapper;
import com.jinHan.shop.core.goodsBrand.domain.model.GoodsBrand;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 类名: GoodsBrandCreateHandler
 * 描述: 新增商品品牌处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/23
 */
@Component
public class GoodsBrandCreateHandler {

    @Resource
    private GoodsBrandMapper goodsBrandMapper;

    public Long create(GoodsBrandCreateCommand command) {
        GoodsBrand goodsBrand = new GoodsBrand();
        goodsBrand.setName(command.getName());
        goodsBrand.setLogo(command.getLogo());
        goodsBrand.setCategoryId(command.getCategoryId());
        goodsBrand.setSort(command.getSort());
        goodsBrand.setStatus(command.getStatus());

        // 自动提取首字母（若前端未传）
        String firstLetter = command.getFirstLetter();
        if (StringUtils.hasText(firstLetter)) {
            goodsBrand.setFirstLetter(firstLetter.toUpperCase());
        } else {
            goodsBrand.setFirstLetter(extractFirstLetter(command.getName()));
        }

        int inserted = goodsBrandMapper.insert(goodsBrand);
        if (inserted <= 0) {
            throw new BusinessException("创建商品品牌失败");
        }

        return goodsBrand.getId();
    }

    /**
     * 提取品牌名称首字母（大写）
     */
    private String extractFirstLetter(String name) {
        if (!StringUtils.hasText(name)) {
            return "#";
        }
        // 使用拼音工具类提取，若不可用则取第一个字符
        char firstChar = name.trim().charAt(0);
        if (Character.isLetter(firstChar)) {
            return String.valueOf(Character.toUpperCase(firstChar));
        }
        return "#";
    }

}
