package com.jinHan.shop.admin.controller.aiChat;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.aicommerce.starter.aiChat.dto.response.ChatStreamResponse;
import com.aicommerce.starter.aiChat.model.ChatUserTypeEnum;
import com.aicommerce.starter.aiChat.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.mockito.MockedStatic;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class AiChatControllerTest {

    @Test
    void shouldReturnProtocolCompliantSseEventsImmediately() throws Exception {
        ChatService chatService = mock(ChatService.class);
        doAnswer(invocation -> {
            SseEmitter emitter = invocation.getArgument(5);
            emitter.send(SseEmitter.event()
                    .name("message")
                    .data(new ChatStreamResponse("第一行\n第二行"), MediaType.APPLICATION_JSON));
            emitter.send(SseEmitter.event()
                    .name("done")
                    .data(new ChatStreamResponse("[DONE]"), MediaType.APPLICATION_JSON));
            emitter.complete();
            return null;
        }).when(chatService).chat(
                eq(ChatUserTypeEnum.ADMIN),
                anyLong(),
                anyLong(),
                anyString(),
                anyString(),
                any(SseEmitter.class));

        AiChatController controller = new AiChatController();
        ReflectionTestUtils.setField(controller, "chatService", chatService);
        MockMvc mockMvc = standaloneSetup(controller).build();

        MvcResult mvcResult;
        try (MockedStatic<StpUtil> stpUtil = mockStatic(StpUtil.class)) {
            stpUtil.when(StpUtil::getLoginIdAsLong).thenReturn(1001L);
            mvcResult = mockMvc.perform(post("/ai/chat")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.TEXT_EVENT_STREAM)
                            .content("{\"modelId\":1,\"sessionId\":\"session-001\",\"message\":\"第一行\\n第二行\"}"))
                    .andExpect(request().asyncStarted())
                    .andReturn();
        }

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
                .andExpect(header().string("Cache-Control", "no-cache, no-transform"))
                .andExpect(header().string("X-Accel-Buffering", "no"))
                .andExpect(content().bytes((":connected\n\n"
                        + "event:message\n"
                        + "data:{\"content\":\"第一行\\n第二行\"}\n\n"
                        + "event:done\n"
                        + "data:{\"content\":\"[DONE]\"}\n\n")
                        .getBytes(StandardCharsets.UTF_8)));

        verify(chatService).chat(
                eq(ChatUserTypeEnum.ADMIN),
                eq(1001L),
                eq(1L),
                eq("session-001"),
                eq("第一行\n第二行"),
                any(SseEmitter.class));
    }

    @Test
    void shouldKeepSseFormatWhenAuthenticationFails() throws Exception {
        ChatService chatService = mock(ChatService.class);
        AiChatController controller = new AiChatController();
        ReflectionTestUtils.setField(controller, "chatService", chatService);

        HandlerInterceptor authenticationInterceptor = new HandlerInterceptor() {
            @Override
            public boolean preHandle(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    Object handler) {
                throw NotLoginException.newInstance(
                        "login",
                        NotLoginException.NOT_TOKEN,
                        "未能读取到有效 token",
                        null);
            }
        };
        MockMvc mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new AiChatExceptionHandler(new ObjectMapper()))
                .addInterceptors(authenticationInterceptor)
                .build();

        mockMvc.perform(post("/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_EVENT_STREAM)
                        .content("{\"modelId\":1,\"sessionId\":\"session-001\",\"message\":\"你好\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
                .andExpect(header().string("Cache-Control", "no-cache, no-transform"))
                .andExpect(header().string("X-Accel-Buffering", "no"))
                .andExpect(content().bytes(("event:error\n"
                        + "data:{\"code\":\"401\",\"msg\":\"未能读取到有效 token\",\"data\":null}\n\n")
                        .getBytes(StandardCharsets.UTF_8)));

        verifyNoInteractions(chatService);
    }

    @Test
    void shouldRejectRequestWithoutSessionId() throws Exception {
        ChatService chatService = mock(ChatService.class);
        AiChatController controller = new AiChatController();
        ReflectionTestUtils.setField(controller, "chatService", chatService);
        MockMvc mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new AiChatExceptionHandler(new ObjectMapper()))
                .build();

        mockMvc.perform(post("/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_EVENT_STREAM)
                        .content("{\"modelId\":1,\"message\":\"你好\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
                .andExpect(content().bytes(("event:error\n"
                        + "data:{\"code\":\"400\",\"msg\":\"会话ID不能为空\",\"data\":null}\n\n")
                        .getBytes(StandardCharsets.UTF_8)));

        verifyNoInteractions(chatService);
    }

    @Test
    void shouldRejectMalformedJsonWithUnescapedLineBreak() throws Exception {
        ChatService chatService = mock(ChatService.class);
        AiChatController controller = new AiChatController();
        ReflectionTestUtils.setField(controller, "chatService", chatService);
        MockMvc mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new AiChatExceptionHandler(new ObjectMapper()))
                .build();

        String malformedJson = """
                {"modelId":1,"sessionId":"session-001","message":"第一行
                第二行"}
                """;

        mockMvc.perform(post("/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_EVENT_STREAM)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
                .andExpect(content().bytes(("event:error\n"
                        + "data:{\"code\":\"400\",\"msg\":\"请求JSON格式错误，多行消息请使用标准JSON序列化，不要手工拼接请求体\",\"data\":null}\n\n")
                        .getBytes(StandardCharsets.UTF_8)));

        verifyNoInteractions(chatService);
    }
}
