package com.aicommerce.starter.aiChat.tool.browser;

import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.service.tool.ToolProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 单轮聊天使用的 Browser MCP 客户端和工具提供器。
 */
@Slf4j
public class BrowserMcpSession implements AutoCloseable {

    private final McpClient client;
    private final ToolProvider toolProvider;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    BrowserMcpSession(McpClient client, ToolProvider toolProvider) {
        this.client = client;
        this.toolProvider = toolProvider;
    }

    public ToolProvider getToolProvider() {
        return toolProvider;
    }

    @Override
    public void close() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        try {
            client.close();
        } catch (Exception exception) {
            log.debug("关闭Browser MCP会话失败: {}", exception.getMessage());
        }
    }
}
