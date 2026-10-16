package com.jinHan.shop.core.ragBucket.domain.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jinHan.shop.core.ragBucket.domain.command.RagBucketListCommand;
import com.jinHan.shop.core.ragBucket.domain.mapper.RagBucketMapper;
import com.jinHan.shop.core.ragBucket.domain.model.RagBucket;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 类名: RagBucketListHandler
 * 描述: 获取 rag 桶处理器
 * 作者: xuzeyu
 * 创建时间: 2026/10/16
 */
@Component
public class RagBucketListHandler {

    @Resource
    private RagBucketMapper ragBucketMapper;


    public List<RagBucket> bucketList(RagBucketListCommand command){
        return ragBucketMapper.selectList(
                new LambdaQueryWrapper<RagBucket>()
                        .eq(RagBucket::getUserId, command.getUserId())
        );
    }
}
