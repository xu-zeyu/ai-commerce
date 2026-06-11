package com.aicommerce.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodeEnum  implements CommonErrorCode {
    SUCCESS("200","操作成功"),
    FAIL("400","操作失败");

    private final String code;
    private final String message;
}
