package com.aicommerce.starter.aiChat.tool.browser;

import com.aicommerce.starter.aiChat.config.BrowserMcpProperties;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.service.tool.ToolProvider;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

/**
 * 为单轮 AI 聊天创建独立的 Browser MCP 会话。
 */
@Component
@Slf4j
public class BrowserMcpToolProviderFactory {

    private static final String CLIENT_KEY = "browser-mcp";
    private static final String CLIENT_NAME = "ai-commerce";

    private final BrowserMcpProperties properties;

    public BrowserMcpToolProviderFactory(BrowserMcpProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void logBrowserMcpConfiguration() {
        log.info(
                "Browser MCP配置: enabled={}, endpoint={}, forceToolCallOnBrowserIntent={}",
                properties.isEnabled(),
                maskEndpoint(properties.getEndpoint()),
                properties.isForceToolCallOnBrowserIntent());
    }

    public boolean isEnabled() {
        return properties.isEnabled();
    }

    public int getMaxToolRoundTrips() {
        return properties.getMaxToolRoundTrips();
    }

    public boolean shouldForceToolCallOnBrowserIntent() {
        return properties.isForceToolCallOnBrowserIntent();
    }

    public BrowserMcpSession createSession() {
        String endpoint = validateEndpoint(properties.getEndpoint());
        Duration timeout = Duration.ofMillis(properties.getTimeoutMillis());
        McpTransport transport = StreamableHttpMcpTransport.builder()
                .url(endpoint)
                .customHeaders(createHeaders())
                .timeout(timeout)
                .build();

        McpClient client = null;
        try {
            client = DefaultMcpClient.builder()
                    .key(CLIENT_KEY)
                    .clientName(CLIENT_NAME)
                    .transport(transport)
                    .initializationTimeout(timeout)
                    .toolExecutionTimeout(timeout)
                    .autoHealthCheck(false)
                    .build();
            ToolProvider toolProvider = McpToolProvider.builder()
                    .mcpClients(client)
                    .failIfOneServerFails(true)
                    .build();
            return new BrowserMcpSession(client, toolProvider);
        } catch (RuntimeException exception) {
            closeQuietly(client == null ? transport : client);
            throw new IllegalStateException("连接Browser MCP服务失败", exception);
        }
    }

    private Map<String, String> createHeaders() {
        String bearerToken = properties.getBearerToken();
        if (bearerToken == null || bearerToken.isBlank()) {
            return Map.of();
        }
        return Map.of("Authorization", "Bearer " + bearerToken.trim());
    }

    private String validateEndpoint(String endpoint) {
        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalStateException("启用Browser MCP时必须配置ai.chat.browser-mcp.endpoint");
        }

        URI uri;
        try {
            uri = URI.create(endpoint.trim());
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("Browser MCP端点格式无效", exception);
        }
        if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))) {
            throw new IllegalStateException("Browser MCP端点只支持HTTP或HTTPS");
        }
        if (uri.getHost() == null || uri.getHost().isBlank()) {
            throw new IllegalStateException("Browser MCP端点缺少有效主机名");
        }
        if (uri.getUserInfo() != null) {
            throw new IllegalStateException("Browser MCP端点不能包含用户名或密码");
        }
        return uri.toString();
    }

    private String maskEndpoint(String endpoint) {
        if (endpoint == null || endpoint.isBlank()) {
            return "(未配置)";
        }
        try {
            URI uri = URI.create(endpoint.trim());
            String port = uri.getPort() < 0 ? "" : ":" + uri.getPort();
            return uri.getScheme() + "://" + uri.getHost() + port + uri.getPath();
        } catch (IllegalArgumentException exception) {
            return "(格式无效)";
        }
    }

    private void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception closeException) {
            log.debug("关闭Browser MCP连接失败: {}", closeException.getMessage());
        }
    }
}
