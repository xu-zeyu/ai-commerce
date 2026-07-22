package com.aicommerce.starter.aiChat.mapper;

import com.aicommerce.starter.aiChat.entity.AiChatMemoryEntity;
import com.aicommerce.starter.mybatis.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户聊天记忆数据访问层。
 */
@Mapper
public interface AiChatMemoryMapper extends BaseMapperX<AiChatMemoryEntity> {
}
