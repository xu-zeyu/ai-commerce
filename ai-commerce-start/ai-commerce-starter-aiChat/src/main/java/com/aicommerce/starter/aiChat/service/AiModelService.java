package com.aicommerce.starter.aiChat.service;

import com.aicommerce.starter.aiChat.entity.AiModelEntity;

import java.util.List;

/**
 * AI 模型查询服务。
 */
public interface AiModelService {

    /**
     * 查询全部已启用的 AI 模型。
     */
    List<AiModelEntity> listAvailable();
}
