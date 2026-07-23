package com.aicommerce.starter.aiChat.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Browser MCP 客户端配置。
 */
@Data
@Validated
@ConfigurationProperties(prefix = "ai.chat.browser-mcp")
public class BrowserMcpProperties {

    /**
     * 是否向 AI 模型开放 Browser MCP 工具。
     */
    private boolean enabled = true;

    /**
     * Browser MCP 服务的 Streamable HTTP 端点。
     */
    private String endpoint;

    /**
     * 可选的 Bearer Token，不配置时不发送 Authorization 请求头。
     */
    private String bearerToken;

    /**
     * MCP 初始化、HTTP 连接和单次工具执行的超时时间。
     */
    @Min(1000)
    @Max(300000)
    private int timeoutMillis = 60000;

    /**
     * 单轮聊天允许的最大工具往返次数。
     */
    @Min(1)
    @Max(30)
    private int maxToolRoundTrips = 12;

    /**
     * 检测到明确的浏览器操作意图时，是否强制首轮模型响应使用工具调用。
     *
     * <p>部分OpenAI兼容模型即使收到了tools，也不会在AUTO模式下主动生成tool_calls，
     * 开启后会对浏览器操作请求使用REQUIRED，工具执行后的后续请求仍恢复为AUTO。</p>
     */
    private boolean forceToolCallOnBrowserIntent = true;
}
