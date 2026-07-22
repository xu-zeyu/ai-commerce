package com.jinHan.shop.admin.controller.aiChat;

import cn.dev33.satoken.stp.StpUtil;
import com.aicommerce.starter.aiChat.dto.request.ChatRequest;
import com.aicommerce.starter.aiChat.model.ChatUserTypeEnum;
import com.aicommerce.starter.aiChat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
@Tag(name = "AI聊天管理")
public class AiChatController {

    private static final long SSE_TIMEOUT_MILLIS = 0L;

    @Resource
    private ChatService chatService;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "AI聊天")
    public ResponseEntity<SseEmitter> aiChat(@Valid @RequestBody ChatRequest request) throws IOException {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MILLIS);

        // 先发送注释帧，尽早提交响应头并确认 SSE 连接已经建立。
        emitter.send(SseEmitter.event().comment("connected"));
        chatService.chat(
                ChatUserTypeEnum.ADMIN,
                StpUtil.getLoginIdAsLong(),
                request.getModelId(),
                request.getSessionId(),
                request.getMessage(),
                emitter);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .cacheControl(CacheControl.noCache().noTransform())
                .header("X-Accel-Buffering", "no")
                .body(emitter);
    }
}
