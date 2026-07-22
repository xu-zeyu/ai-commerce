-- 仅用于已经创建过 ai_chat_memory 表的环境。
-- 原有记忆统一归入 default 会话，执行完成后新请求应传入明确的 sessionId。
ALTER TABLE `ai_chat_memory`
    ADD COLUMN `session_id` VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT '会话ID' AFTER `model_id`;

ALTER TABLE `ai_chat_memory`
    DROP INDEX `uk_ai_chat_memory_user_model`,
    ADD UNIQUE KEY `uk_ai_chat_memory_user_model_session`
        (`user_type`, `user_id`, `model_id`, `session_id`);

ALTER TABLE `ai_chat_memory`
    ALTER COLUMN `session_id` DROP DEFAULT;
