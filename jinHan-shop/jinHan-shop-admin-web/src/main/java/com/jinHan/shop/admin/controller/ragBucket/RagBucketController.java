package com.jinHan.shop.admin.controller.ragBucket;

import cn.dev33.satoken.stp.StpUtil;
import com.aicommerce.common.model.Result;
import com.aicommerce.log.annotation.Log;
import com.jinHan.shop.admin.controller.ragBucket.dto.request.RagBucketCreateRequest;
import com.jinHan.shop.core.ragBucket.domain.command.RagBucketCreateCommand;
import com.jinHan.shop.core.ragBucket.domain.command.RagBucketListCommand;
import com.jinHan.shop.core.ragBucket.domain.handler.RagBucketCreateHandler;
import com.jinHan.shop.core.ragBucket.domain.handler.RagBucketListHandler;
import com.jinHan.shop.core.ragBucket.domain.model.RagBucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 类名: RagBucketController
 * 描述: 创建 rag 桶
 * 作者: xuzeyu
 * 创建时间: 2026/10/14
 */
@Validated
@RestController
@RequestMapping("/rag/bucket")
@Tag(name = "rag 桶管理")
public class RagBucketController {

    @Resource
    private RagBucketCreateHandler ragBucketCreateHandler;

    @Resource
    private RagBucketListHandler ragBucketListHandler;

    @Log(value = "创建RAG桶", operationType = "RAGBUCKET_CREATE")
    @Operation(summary = "创建RAG桶")
    @PostMapping("/create")
    public Result<RagBucket> create(@RequestBody @Valid RagBucketCreateRequest request) {
        String userId = StpUtil.getLoginId().toString();
        RagBucketCreateCommand command = request.toCommand(userId, request);
        return Result.success(ragBucketCreateHandler.create(command));
    }


    @Log(value = "获取RAG桶", operationType = "RAGBUCKET_LIST")
    @Operation(summary = "获取RAG桶")
    @GetMapping("/list")
    public Result<List<RagBucket>> bucketList() {
        String userId = StpUtil.getLoginId().toString();
        RagBucketListCommand command = new RagBucketListCommand(Long.valueOf(userId));
        return  Result.success(ragBucketListHandler.bucketList(command));
    }
}
