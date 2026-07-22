package com.jinHan.shop.admin.controller.aiChat;

import com.aicommerce.starter.aiChat.entity.AiModelEntity;
import com.aicommerce.starter.aiChat.service.AiModelService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class AiModelControllerTest {

    @Test
    void shouldReturnAvailableModelsWithoutInternalConfiguration() throws Exception {
        AiModelEntity model = new AiModelEntity(
                1L,
                "openai",
                "gpt-4.1",
                "https://example.com/v1",
                "secret-api-key",
                true
        );
        AiModelService aiModelService = mock(AiModelService.class);
        when(aiModelService.listAvailable()).thenReturn(List.of(model));

        MockMvc mockMvc = standaloneSetup(new AiModelController(aiModelService)).build();

        mockMvc.perform(get("/ai/model"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].provider").value("openai"))
                .andExpect(jsonPath("$.data[0].modelName").value("gpt-4.1"))
                .andExpect(jsonPath("$.data[0].apiKey").doesNotExist())
                .andExpect(jsonPath("$.data[0].baseUrl").doesNotExist());
    }
}
