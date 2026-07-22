package com.aicommerce.starter.aiChat.store;

import com.aicommerce.starter.aiChat.entity.AiChatMemoryEntity;
import com.aicommerce.starter.aiChat.mapper.AiChatMemoryMapper;
import com.aicommerce.starter.aiChat.model.ChatMemoryId;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 基于 MySQL 的聊天记忆存储。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PersistentChatMemoryStore implements ChatMemoryStore {

    private final AiChatMemoryMapper aiChatMemoryMapper;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        ChatMemoryId chatMemoryId = requireChatMemoryId(memoryId);
        AiChatMemoryEntity entity = findMemory(chatMemoryId);
        if (entity == null || entity.getMessagesJson() == null || entity.getMessagesJson().isBlank()) {
            return Collections.emptyList();
        }

        try {
            return ChatMessageDeserializer.messagesFromJson(entity.getMessagesJson());
        } catch (RuntimeException exception) {
            log.error("聊天记忆数据解析失败，memoryId={}", chatMemoryId, exception);
            throw new IllegalStateException("聊天记忆数据异常", exception);
        }
    }

    @Override
    @Transactional
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        ChatMemoryId chatMemoryId = requireChatMemoryId(memoryId);
        String messagesJson = ChatMessageSerializer.messagesToJson(List.copyOf(messages));
        AiChatMemoryEntity entity = findMemory(chatMemoryId);

        if (entity != null) {
            entity.setMessagesJson(messagesJson);
            aiChatMemoryMapper.updateById(entity);
            return;
        }

        try {
            aiChatMemoryMapper.insert(createEntity(chatMemoryId, messagesJson));
        } catch (DuplicateKeyException exception) {
            // 并发创建同一用户记忆时，唯一索引只保留一行，随后更新即可。
            AiChatMemoryEntity concurrentEntity = findMemory(chatMemoryId);
            if (concurrentEntity == null) {
                throw exception;
            }
            concurrentEntity.setMessagesJson(messagesJson);
            aiChatMemoryMapper.updateById(concurrentEntity);
        }
    }

    @Override
    @Transactional
    public void deleteMessages(Object memoryId) {
        ChatMemoryId chatMemoryId = requireChatMemoryId(memoryId);
        aiChatMemoryMapper.delete(memoryQuery(chatMemoryId));
    }

    private AiChatMemoryEntity findMemory(ChatMemoryId memoryId) {
        return aiChatMemoryMapper.selectOne(memoryQuery(memoryId));
    }

    private LambdaQueryWrapper<AiChatMemoryEntity> memoryQuery(ChatMemoryId memoryId) {
        return new LambdaQueryWrapper<AiChatMemoryEntity>()
                .eq(AiChatMemoryEntity::getUserType, memoryId.userType().name())
                .eq(AiChatMemoryEntity::getUserId, memoryId.userId())
                .eq(AiChatMemoryEntity::getModelId, memoryId.modelId())
                .eq(AiChatMemoryEntity::getSessionId, memoryId.sessionId());
    }

    private AiChatMemoryEntity createEntity(ChatMemoryId memoryId, String messagesJson) {
        AiChatMemoryEntity entity = new AiChatMemoryEntity();
        entity.setUserType(memoryId.userType().name());
        entity.setUserId(memoryId.userId());
        entity.setModelId(memoryId.modelId());
        entity.setSessionId(memoryId.sessionId());
        entity.setMessagesJson(messagesJson);
        return entity;
    }

    private ChatMemoryId requireChatMemoryId(Object memoryId) {
        if (memoryId instanceof ChatMemoryId chatMemoryId) {
            return chatMemoryId;
        }
        throw new IllegalArgumentException("memoryId must be ChatMemoryId");
    }
}
