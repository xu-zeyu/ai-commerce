package com.jinHan.shop.core.ragBucket.domain.command;

import lombok.Data;

/**
 * 类名: RagBucketCreateCommand
 * 描述: 创建 rag 桶命令
 * 作者: xuzeyu
 * 创建时间: 2026/10/14
 */

@Data
public class RagBucketCreateCommand {
    private Long userId;

    private String code;

    private String description;
}
