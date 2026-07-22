package com.jinHan.shop.admin.controller.aiChat;

import cn.dev33.satoken.stp.StpUtil;
import com.aicommerce.common.model.Result;
import com.aicommerce.starter.aiChat.entity.AiChatMemoryEntity;
import com.aicommerce.starter.aiChat.service.AiModelService;
import com.jinHan.shop.admin.controller.aiChat.response.AiMemoryResponse;
import com.jinHan.shop.admin.controller.aiChat.response.AiModelResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 类名: AiChatMemory
 * 描述: 会话记录
 * 作者: xuzeyu
 * 创建时间: 2026/7/22
 */

@RestController
@RequestMapping("/ai/memory")
@Tag(name = "AI会话管理")
public class AiChatMemoryController {

    private final AiModelService aiModelService;

    public AiChatMemoryController(AiModelService aiModelService) {
        this.aiModelService = aiModelService;
    }
    @Operation(summary = "获取全部可用AI模型")
    @GetMapping
    public Result<List<AiMemoryResponse>> listMemory() {
        List<AiMemoryResponse> modelList = aiModelService.listChat(Long.valueOf(StpUtil.getLoginId().toString())).stream()
                .map(AiMemoryResponse::from)
                .collect(Collectors.toList());
        return Result.success(modelList);
    }

}
