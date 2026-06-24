package com.jinHan.shop.core.goodsBrand.domain.handler;

import com.aicommerce.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jinHan.shop.core.goodsBrand.domain.mapper.GoodsBrandMapper;
import com.jinHan.shop.core.goodsBrand.domain.model.GoodsBrand;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 类名: GoodsBrandDeleteHandler
 * 描述: 删除商品品牌处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/23
 */
@Component
public class GoodsBrandDeleteHandler {

    @Resource
    private GoodsBrandMapper goodsBrandMapper;

    public void delete(Long id) {
        GoodsBrand brand = goodsBrandMapper.selectById(id);
        if (brand == null) {
            throw new BusinessException("商品品牌不存在");
        }

        int result = goodsBrandMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除商品品牌失败");
        }
    }

}
