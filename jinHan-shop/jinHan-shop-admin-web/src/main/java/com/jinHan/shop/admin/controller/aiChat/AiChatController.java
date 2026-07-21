package com.jinHan.shop.admin.controller.aiChat;

import com.aicommerce.log.annotation.Log;
import com.aicommerce.starter.aiChat.dto.request.ChatRequest;
import com.aicommerce.starter.aiChat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/ai")
@Tag(name = "AI聊天管理")
public class AiChatController {

    @Resource
    private ChatService chatService;

    @PostMapping(value = "/chat",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "AI聊天")
    public SseEmitter aiChat(@RequestBody ChatRequest request){

        SseEmitter emitter = new SseEmitter(0L);

        chatService.chat(request.getModelId(),request.getMessage(), emitter);

        return emitter;
    }
}
