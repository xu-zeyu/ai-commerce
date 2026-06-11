package com.aicommerce.auth.domain.model;

import lombok.Getter;

/**
 * 用户认证状态枚举
 * 支持扩展新的认证状态
 */
@Getter
public enum UsersStatusEnum {
    /**
     * 未认证：用户尚未提交认证信息
     */
    UNAUTHENTICATED,

    /**
     * 审核中：用户已提交认证信息，等待审核
     */
    UNDER_REVIEW,

    /**
     * 已完成：认证已通过
     */
    COMPLETED,

    /**
     * 已拒绝：认证未通过
     */
    REJECTED;

}
