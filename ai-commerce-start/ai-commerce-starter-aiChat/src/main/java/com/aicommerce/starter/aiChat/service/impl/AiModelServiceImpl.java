package com.aicommerce.starter.aiChat.service.impl;

import com.aicommerce.auth.stp.StpUserUtil;
import com.aicommerce.starter.aiChat.entity.AiChatMemoryEntity;
import com.aicommerce.starter.aiChat.entity.AiModelEntity;
import com.aicommerce.starter.aiChat.mapper.AiChatMemoryMapper;
import com.aicommerce.starter.aiChat.mapper.AiModelMapper;
import com.aicommerce.starter.aiChat.service.AiModelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI 模型查询服务实现。
 */
@Service
public class AiModelServiceImpl implements AiModelService {

    @Resource
    private AiModelMapper aiModelMapper;

    @Resource
    private AiChatMemoryMapper aiChatMemoryMapper;

    @Override
    public List<AiModelEntity> listAvailable() {
        return aiModelMapper.selectList(
                new LambdaQueryWrapper<AiModelEntity>()
                        .eq(AiModelEntity::getEnabled, Boolean.TRUE)
                        .orderByAsc(AiModelEntity::getId)
        );
    }

    @Override
    public List<AiChatMemoryEntity> listChat(Long userId) {
        return aiChatMemoryMapper.selectList(
                new LambdaQueryWrapper<AiChatMemoryEntity>()
                        .eq(AiChatMemoryEntity::getUserId, userId)
                        .orderByAsc(AiChatMemoryEntity::getId)
        );
    }
}
