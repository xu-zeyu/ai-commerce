package com.aicommerce.starter.aiChat.entity;

import com.aicommerce.starter.mybatis.model.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户聊天记忆实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_chat_memory")
public class AiChatMemoryEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userType;

    private Long userId;

    private Long modelId;

    private String sessionId;

    private String messagesJson;
}
