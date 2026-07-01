package com.jinHan.shop.core.product.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * SKU 状态枚举
 *
 * @author xuzeyu
 * @since 2026-07-01
 */
@Getter
public enum SkuStatusEnum {
    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    @EnumValue
    private final Integer code;
    private final String desc;

    SkuStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
