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

    /**
     * 业务异常属于预期内的流程控制，不是程序缺陷，其堆栈无排查价值。
     * 重写后不再采集调用栈，日志里只保留一行提示，同时避免填充堆栈带来的性能开销。
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
