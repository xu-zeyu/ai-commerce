package com.jinHan.shop.admin.controller.aiChat;

import com.aicommerce.common.model.Result;
import com.aicommerce.starter.aiChat.service.AiModelService;
import com.jinHan.shop.admin.controller.aiChat.response.AiModelResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 模型接口。
 */
@RestController
@RequestMapping("/ai/model")
@Tag(name = "AI模型管理")
public class AiModelController {

    private final AiModelService aiModelService;

    public AiModelController(AiModelService aiModelService) {
        this.aiModelService = aiModelService;
    }

    @Operation(summary = "获取全部可用AI模型")
    @GetMapping
    public Result<List<AiModelResponse>> listAvailable() {
        List<AiModelResponse> modelList = aiModelService.listAvailable().stream()
                .map(AiModelResponse::from)
                .collect(Collectors.toList());
        return Result.success(modelList);
    }
}
