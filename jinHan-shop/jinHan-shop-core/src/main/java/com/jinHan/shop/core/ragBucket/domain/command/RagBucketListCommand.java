package com.jinHan.shop.core.ragBucket.domain.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名: RagBucketListCommand
 * 描述: 当前用户的 rag 桶
 * 作者: xuzeyu
 * 创建时间: 2026/10/16
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RagBucketListCommand {
    private Long userId;
}
