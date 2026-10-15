package com.jinHan.shop.admin.controller.ragBucket.dto.request;

import com.jinHan.shop.core.ragBucket.domain.command.RagBucketCreateCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 类名: RagBucketCreateRequest
 * 描述: RAG桶创建请求参数
 * 作者: xuzeyu
 * 创建时间: 2026/10/14
 */

@Data
public class RagBucketCreateRequest {
    @NotBlank(message = "桶名不能为空")
    private String code;
    @NotBlank(message = "桶描述不能为空")
    private String description;


    public RagBucketCreateCommand toCommand(String userId , RagBucketCreateRequest request){
        RagBucketCreateCommand ragBucketCreateCommand = new RagBucketCreateCommand();
        ragBucketCreateCommand.setUserId(Long.valueOf(userId));
        ragBucketCreateCommand.setCode(request.getCode());
        ragBucketCreateCommand.setDescription(request.getDescription());
        return ragBucketCreateCommand;
    }
}
