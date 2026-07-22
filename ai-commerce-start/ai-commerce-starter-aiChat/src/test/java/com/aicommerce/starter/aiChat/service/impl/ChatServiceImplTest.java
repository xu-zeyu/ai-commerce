package com.aicommerce.starter.aiChat.service.impl;

import com.aicommerce.starter.aiChat.entity.AiModelEntity;
import com.aicommerce.starter.aiChat.factory.ModelFactory;
import com.aicommerce.starter.aiChat.mapper.AiModelMapper;
import com.aicommerce.starter.aiChat.model.ChatMemoryId;
import com.aicommerce.starter.aiChat.model.ChatUserTypeEnum;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    private static final ChatMemoryId MEMORY_ID =
            new ChatMemoryId(ChatUserTypeEnum.ADMIN, 1001L, 1L, "session-a");

    @Mock
    private ModelFactory modelFactory;

    @Mock
    private AiModelMapper aiModelMapper;

    @Mock
    private StreamingChatModel streamingChatModel;

    private InMemoryChatMemoryStore chatMemoryStore;

    private ChatServiceImpl chatService;

    @BeforeEach
    void setUp() {
        chatMemoryStore = new InMemoryChatMemoryStore();
        chatService = new ChatServiceImpl();
        ReflectionTestUtils.setField(chatService, "modelFactory", modelFactory);
        ReflectionTestUtils.setField(chatService, "aiModelMapper", aiModelMapper);
        ReflectionTestUtils.setField(chatService, "chatMemoryStore", chatMemoryStore);
        ReflectionTestUtils.setField(chatService, "maxMemoryTokens", 4000);
        ReflectionTestUtils.setField(chatService, "fallbackTokenizerModel", "gpt-4o-mini");

        AiModelEntity model = new AiModelEntity();
        model.setId(1L);
        model.setModelName("gpt-4o-mini");
        when(aiModelMapper.selectById(1L)).thenReturn(model);
        when(modelFactory.createChatModel(model)).thenReturn(streamingChatModel);
    }

    @Test
    void shouldSendUserHistoryToModelAndPersistCompletedTurn() {
        ChatMemoryId otherUserMemoryId =
                new ChatMemoryId(ChatUserTypeEnum.ADMIN, 1002L, 1L, "session-a");
        ChatMemoryId otherSessionMemoryId =
                new ChatMemoryId(ChatUserTypeEnum.ADMIN, 1001L, 1L, "session-b");
        chatMemoryStore.updateMessages(otherUserMemoryId, List.of(
                UserMessage.from("另一个用户的私密信息"),
                AiMessage.from("只应该属于另一个用户")));
        chatMemoryStore.updateMessages(otherSessionMemoryId, List.of(
                UserMessage.from("同一用户的另一个会话"),
                AiMessage.from("不应该进入当前会话")));
        chatMemoryStore.updateMessages(MEMORY_ID, List.of(
                UserMessage.from("我喜欢黑咖啡"),
                AiMessage.from("我记住了")));
        AtomicReference<List<ChatMessage>> requestMessages = new AtomicReference<>();
        doAnswer(invocation -> {
            requestMessages.set(List.copyOf(invocation.getArgument(0)));
            StreamingChatResponseHandler handler = invocation.getArgument(1);
            handler.onPartialResponse("你喜欢");
            handler.onCompleteResponse(ChatResponse.builder()
                    .aiMessage(AiMessage.from("你喜欢黑咖啡"))
                    .build());
            return null;
        }).when(streamingChatModel).chat(anyList(), any(StreamingChatResponseHandler.class));

        chatService.chat(
                ChatUserTypeEnum.ADMIN,
                1001L,
                1L,
                "session-a",
                "我喜欢什么？",
                new SseEmitter());

        assertThat(requestMessages.get()).containsExactly(
                UserMessage.from("我喜欢黑咖啡"),
                AiMessage.from("我记住了"),
                UserMessage.from("我喜欢什么？"));
        assertThat(chatMemoryStore.getMessages(MEMORY_ID)).containsExactly(
                UserMessage.from("我喜欢黑咖啡"),
                AiMessage.from("我记住了"),
                UserMessage.from("我喜欢什么？"),
                AiMessage.from("你喜欢黑咖啡"));
        assertThat(chatMemoryStore.getMessages(otherUserMemoryId)).containsExactly(
                UserMessage.from("另一个用户的私密信息"),
                AiMessage.from("只应该属于另一个用户"));
        assertThat(chatMemoryStore.getMessages(otherSessionMemoryId)).containsExactly(
                UserMessage.from("同一用户的另一个会话"),
                AiMessage.from("不应该进入当前会话"));
    }

    @Test
    void shouldNotPersistIncompleteTurnWhenModelFails() {
        List<ChatMessage> originalMessages = List.of(
                UserMessage.from("上一个问题"),
                AiMessage.from("上一个回答"));
        chatMemoryStore.updateMessages(MEMORY_ID, originalMessages);
        doAnswer(invocation -> {
            StreamingChatResponseHandler handler = invocation.getArgument(1);
            handler.onError(new IllegalStateException("模型异常"));
            return null;
        }).when(streamingChatModel).chat(anyList(), any(StreamingChatResponseHandler.class));

        chatService.chat(
                ChatUserTypeEnum.ADMIN,
                1001L,
                1L,
                "session-a",
                "这一轮会失败",
                new SseEmitter());

        assertThat(chatMemoryStore.getMessages(MEMORY_ID)).containsExactlyElementsOf(originalMessages);
    }

    @Test
    void shouldUseFallbackTokenizerForOpenAiCompatibleModel() {
        AiModelEntity model = aiModelMapper.selectById(1L);
        model.setModelName("deepseek-v4-pro");
        AtomicReference<List<ChatMessage>> requestMessages = new AtomicReference<>();
        doAnswer(invocation -> {
            requestMessages.set(List.copyOf(invocation.getArgument(0)));
            StreamingChatResponseHandler handler = invocation.getArgument(1);
            handler.onCompleteResponse(ChatResponse.builder()
                    .aiMessage(AiMessage.from("回退分词器可用"))
                    .build());
            return null;
        }).when(streamingChatModel).chat(anyList(), any(StreamingChatResponseHandler.class));

        chatService.chat(
                ChatUserTypeEnum.ADMIN,
                1001L,
                1L,
                "session-a",
                "测试DeepSeek模型",
                new SseEmitter());

        assertThat(requestMessages.get()).containsExactly(UserMessage.from("测试DeepSeek模型"));
        assertThat(chatMemoryStore.getMessages(MEMORY_ID)).containsExactly(
                UserMessage.from("测试DeepSeek模型"),
                AiMessage.from("回退分词器可用"));
    }
}
