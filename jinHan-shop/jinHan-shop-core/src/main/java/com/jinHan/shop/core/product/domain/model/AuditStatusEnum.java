package com.jinHan.shop.core.product.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 商品审核状态枚举
 *
 * @author xuzeyu
 * @since 2026-06-25
 */
@Getter
public enum AuditStatusEnum {
    PENDING(0, "待审核"),
    PASS(1, "审核通过"),
    REJECT(2, "审核拒绝");

    @EnumValue
    private final Integer code;
    private final String desc;

    AuditStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
