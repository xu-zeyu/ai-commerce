package com.jinHan.shop.admin.controller.aiChat;

import cn.dev33.satoken.exception.NotLoginException;
import com.aicommerce.common.model.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.charset.StandardCharsets;

/**
 * 保证 AI 聊天接口发生异常时仍返回合法的 SSE 事件。
 *
 * @author xuzeyu
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@RestControllerAdvice(assignableTypes = AiChatController.class)
public class AiChatExceptionHandler {

    private static final String DEFAULT_ERROR_MESSAGE = "AI聊天服务异常，请稍后重试";
    private static final MediaType SSE_MEDIA_TYPE = new MediaType(
            MediaType.TEXT_EVENT_STREAM,
            StandardCharsets.UTF_8);

    private final ObjectMapper objectMapper;

    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<String> handleNoLoginException(NotLoginException exception) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.isBindingFailure()
                        ? "参数类型错误：" + fieldError.getField() + " 类型不正确"
                        : fieldError.getDefaultMessage())
                .orElse("参数错误");
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception) {
        log.warn("AI聊天请求JSON格式错误: {}", exception.getMessage());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "请求JSON格式错误，多行消息请使用标准JSON序列化，不要手工拼接请求体");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.error("AI聊天接口异常", exception);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, DEFAULT_ERROR_MESSAGE);
    }

    private ResponseEntity<String> buildErrorResponse(HttpStatus status, String message) {
        Result<Void> result = Result.build(String.valueOf(status.value()), message);
        return ResponseEntity.status(status)
                .contentType(SSE_MEDIA_TYPE)
                .cacheControl(CacheControl.noCache().noTransform())
                .header("X-Accel-Buffering", "no")
                .body(toSseErrorEvent(result));
    }

    private String toSseErrorEvent(Result<Void> result) {
        try {
            return "event:error\n" + "data:" + objectMapper.writeValueAsString(result) + "\n\n";
        } catch (JsonProcessingException exception) {
            log.error("序列化SSE错误事件失败", exception);
            return "event:error\n"
                    + "data:{\"code\":\"500\",\"msg\":\"AI聊天服务异常，请稍后重试\",\"data\":null}\n\n";
        }
    }
}
