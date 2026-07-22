package com.aicommerce.starter.aiChat.store;

import com.aicommerce.starter.aiChat.entity.AiChatMemoryEntity;
import com.aicommerce.starter.aiChat.mapper.AiChatMemoryMapper;
import com.aicommerce.starter.aiChat.model.ChatMemoryId;
import com.aicommerce.starter.aiChat.model.ChatUserTypeEnum;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.UserMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersistentChatMemoryStoreTest {

    @Mock
    private AiChatMemoryMapper aiChatMemoryMapper;

    @InjectMocks
    private PersistentChatMemoryStore chatMemoryStore;

    @Test
    void shouldRestoreMessagesForSpecifiedUserAndModel() {
        List<ChatMessage> expectedMessages = List.of(
                UserMessage.from("我喜欢黑咖啡"),
                AiMessage.from("我记住了"));
        AiChatMemoryEntity entity = new AiChatMemoryEntity();
        entity.setMessagesJson(ChatMessageSerializer.messagesToJson(expectedMessages));
        when(aiChatMemoryMapper.selectOne(any())).thenReturn(entity);

        List<ChatMessage> actualMessages = chatMemoryStore.getMessages(
                new ChatMemoryId(ChatUserTypeEnum.ADMIN, 1001L, 1L, "session-a"));

        assertThat(actualMessages).containsExactlyElementsOf(expectedMessages);
    }

    @Test
    void shouldCreateMemoryWhenUserHasNoExistingRecord() {
        when(aiChatMemoryMapper.selectOne(any())).thenReturn(null);
        List<ChatMessage> messages = List.of(
                UserMessage.from("请记住我的称呼"),
                AiMessage.from("好的"));

        chatMemoryStore.updateMessages(
                new ChatMemoryId(ChatUserTypeEnum.USER, 2002L, 3L, "session-b"),
                messages);

        ArgumentCaptor<AiChatMemoryEntity> entityCaptor = ArgumentCaptor.forClass(AiChatMemoryEntity.class);
        verify(aiChatMemoryMapper).insert(entityCaptor.capture());
        AiChatMemoryEntity savedEntity = entityCaptor.getValue();
        assertThat(savedEntity.getUserType()).isEqualTo("USER");
        assertThat(savedEntity.getUserId()).isEqualTo(2002L);
        assertThat(savedEntity.getModelId()).isEqualTo(3L);
        assertThat(savedEntity.getSessionId()).isEqualTo("session-b");
        assertThat(ChatMessageDeserializer.messagesFromJson(savedEntity.getMessagesJson()))
                .containsExactlyElementsOf(messages);
    }
}
