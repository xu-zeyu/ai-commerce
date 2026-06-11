package com.aicommerce.common.exception;


import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final CommonErrorCode commonErrorCode;

    public BusinessException(String message) {
        super(message);
        this.commonErrorCode = ErrorCodeEnum.FAIL;
    }

    public BusinessException(CommonErrorCode commonErrorCode) {
      super(commonErrorCode.getMessage());
      this.commonErrorCode = commonErrorCode;
    }

    public BusinessException(CommonErrorCode commonErrorCode, String message) {
      super(message);
      this.commonErrorCode = commonErrorCode;
    }

    public BusinessException(Throwable cause,String message) {
      super(message, cause);
      this.commonErrorCode = ErrorCodeEnum.FAIL;
    }

    public BusinessException(String messageTemplate, Object... args) {
        super(String.format(messageTemplate, args));
        this.commonErrorCode = ErrorCodeEnum.FAIL;
    }

}
