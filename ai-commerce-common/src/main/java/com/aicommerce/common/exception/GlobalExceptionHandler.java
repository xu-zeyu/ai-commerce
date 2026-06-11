package com.aicommerce.common.exception;
import cn.dev33.satoken.exception.NotLoginException;
import com.aicommerce.common.model.Result;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

/**
 * 类名: GlobalExceptionHandler
 * 描述: 全局异常处理
 * 作者: xuzeyu
 * 创建时间: 2025/12/22
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NotNull HttpHeaders headers,
            HttpStatusCode status, // 修改点1：HttpStatus -> HttpStatusCode
            @NotNull WebRequest request
    ) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("参数错误");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Result.build(String.valueOf(status.value()), errorMessage));
    }

    /**
     * 处理业务异常
     * 返回 HTTP 400 Bad Request
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        // 业务异常属于预期内的流程控制，只打一行提示；末尾不要传 e，否则会打印 BusinessException 那一行
        log.warn("业务异常 - 错误码: {}, 错误信息: {}", e.getCommonErrorCode().getCode(), e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e);
    }

    /**
     * 处理参数校验异常（@Validated 校验失败）
     * 返回 HTTP 400 Bad Request
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        log.error("参数校验异常: {}", errorMessage, e);
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                new BusinessException(ErrorCodeEnum.FAIL, errorMessage)
        );
    }

    /**
     * 处理运行时异常
     * 返回 HTTP 500 Internal Server Error
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<Void>> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                new BusinessException(ErrorCodeEnum.FAIL, e.getMessage())
        );
    }

    /**
     * 处理其他异常
     * 返回 HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("未知异常: {}", e.getMessage(), e);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                new BusinessException(ErrorCodeEnum.FAIL, e.getMessage())
        );
    }


    /**
     * 处理参数校验异常（@Validated 校验失败）
     * 返回 HTTP 400 Bad Request
     */
    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<Result<Void>> handleNoLoginException(NotLoginException e) {
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                new BusinessException(ErrorCodeEnum.FAIL, e.getMessage())
        );
    }

    /**
     * 构建统一的错误响应
     */
    private ResponseEntity<Result<Void>> buildErrorResponse(HttpStatus status, BusinessException e) {
        return ResponseEntity
                .status(status)
                .body(Result.error(e));
    }
}
