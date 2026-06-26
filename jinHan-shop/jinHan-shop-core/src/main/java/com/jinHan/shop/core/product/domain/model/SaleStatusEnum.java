package com.jinHan.shop.core.product.domain.model;

import lombok.Getter;

/**
 * 商品销售状态枚举
 *
 * @author xuzeyu
 * @since 2026-06-25
 */
@Getter
public enum SaleStatusEnum {
    OFF_SHELF(0, "下架"),
    ON_SHELF(1, "上架");

    private final Integer code;
    private final String desc;

    SaleStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
