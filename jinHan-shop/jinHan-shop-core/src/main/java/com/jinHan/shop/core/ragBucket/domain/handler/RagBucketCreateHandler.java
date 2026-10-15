package com.jinHan.shop.core.ragBucket.domain.handler;

import com.aicommerce.common.exception.BusinessException;
import com.jinHan.shop.core.ragBucket.domain.command.RagBucketCreateCommand;
import com.jinHan.shop.core.ragBucket.domain.mapper.RagBucketMapper;
import com.jinHan.shop.core.ragBucket.domain.model.RagBucket;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;


/**
 * 类名: RagBucketCreateHandler
 * 描述: 创建rag 桶处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/15
 */
@Component
public class RagBucketCreateHandler {

    @Resource
    private RagBucketMapper ragBucketMapper;


    /**
     * 创建RAG桶
     */
    public RagBucket create(RagBucketCreateCommand command) {

        try {
            if (command == null) {
                throw new BusinessException("创建参数不能为空");
            }

            if (command.getUserId() == null || command.getUserId() <= 0) {
                throw new BusinessException("userId 无效");
            }

            if (command.getCode() == null
                    || command.getCode().isBlank()) {
                throw new BusinessException("RAG桶编码不能为空");
            }

            RagBucket ragBucket = new RagBucket();
            ragBucket.setCode(command.getCode());
            ragBucket.setUserId(command.getUserId());
            ragBucket.setDescription(command.getDescription());
            int inserted = ragBucketMapper.insert(ragBucket);
            if (inserted <= 0) {
                throw new BusinessException("创建RAG桶失败");
            }
            return ragBucket;
        } catch (DuplicateKeyException e) {
            throw new BusinessException("该知识库已存在");
        }
    }
}
