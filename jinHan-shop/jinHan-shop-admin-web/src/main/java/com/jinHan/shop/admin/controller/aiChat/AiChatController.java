package com.jinHan.shop.admin.controller.aiChat;

import com.aicommerce.starter.aiChat.dto.request.ChatRequest;
import com.aicommerce.starter.aiChat.dto.response.ChatResponse;
import com.aicommerce.starter.aiChat.service.ChatService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class AiChatController {

    @Resource
    private ChatService chatService;

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request){


        String answer = chatService.chat(
                request.getModelId(),
                request.getMessage()
        );

        return new ChatResponse(answer);
    }
}
