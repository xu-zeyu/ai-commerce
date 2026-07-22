CREATE TABLE IF NOT EXISTS `ai_chat_memory`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_type`     VARCHAR(16)     NOT NULL COMMENT '用户类型：ADMIN/USER',
    `user_id`       BIGINT          NOT NULL COMMENT '用户ID',
    `model_id`      BIGINT          NOT NULL COMMENT 'AI模型ID',
    `session_id`    VARCHAR(64)     NOT NULL COMMENT '会话ID',
    `messages_json` MEDIUMTEXT      NOT NULL COMMENT '窗口内聊天消息JSON',
    `created_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ai_chat_memory_user_model_session` (`user_type`, `user_id`, `model_id`, `session_id`),
    KEY `idx_ai_chat_memory_model_id` (`model_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '用户AI聊天记忆';
