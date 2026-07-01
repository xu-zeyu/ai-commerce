package com.jinHan.shop.core.product.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 商品图片类型枚举
 *
 * @author xuzeyu
 * @since 2026-07-01
 */
@Getter
public enum ImageTypeEnum {

    MAIN(1, "主图"),
    CAROUSEL(2, "轮播图"),
    DETAIL(3, "详情图");

    @EnumValue
    private final Integer code;
    private final String desc;

    ImageTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
