package com.aicommerce.starter.aiChat.tool.browser;

import com.aicommerce.starter.aiChat.config.BrowserMcpProperties;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.invocation.InvocationContext;
import dev.langchain4j.service.tool.ToolProviderRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class BrowserMcpToolProviderFactoryTest {

    private static final Pattern REQUEST_ID_PATTERN = Pattern.compile("\\\"id\\\"\\s*:\\s*(\\d+)");

    @Test
    void shouldRequireEndpointWhenBrowserMcpIsEnabled() {
        BrowserMcpProperties properties = new BrowserMcpProperties();
        properties.setEnabled(true);
        BrowserMcpToolProviderFactory factory = new BrowserMcpToolProviderFactory(properties);

        assertThatThrownBy(factory::createSession)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("必须配置");
    }

    @Test
    void shouldRejectUnsupportedEndpointScheme() {
        BrowserMcpProperties properties = new BrowserMcpProperties();
        properties.setEnabled(true);
        properties.setEndpoint("file:///tmp/browser-mcp");
        BrowserMcpToolProviderFactory factory = new BrowserMcpToolProviderFactory(properties);

        assertThatThrownBy(factory::createSession)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("HTTP或HTTPS");
    }

    @Test
    void shouldRejectCredentialsInEndpoint() {
        BrowserMcpProperties properties = new BrowserMcpProperties();
        properties.setEnabled(true);
        properties.setEndpoint("https://user:secret@example.com/mcp");
        BrowserMcpToolProviderFactory factory = new BrowserMcpToolProviderFactory(properties);

        assertThatThrownBy(factory::createSession)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("用户名或密码");
    }

    @Test
    void shouldConnectToStreamableHttpBrowserMcpAndExposeTools() throws IOException {
        AtomicReference<String> authorizationHeader = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/mcp", exchange -> handleMcpRequest(exchange, authorizationHeader));
        server.start();

        BrowserMcpProperties properties = new BrowserMcpProperties();
        properties.setEnabled(true);
        properties.setEndpoint("http://127.0.0.1:" + server.getAddress().getPort() + "/mcp");
        properties.setBearerToken("test-token");
        properties.setTimeoutMillis(5000);
        BrowserMcpToolProviderFactory factory = new BrowserMcpToolProviderFactory(properties);

        try (BrowserMcpSession session = factory.createSession()) {
            UserMessage userMessage = UserMessage.from("打开网页");
            ToolProviderRequest request = ToolProviderRequest.builder()
                    .invocationContext(mock(InvocationContext.class))
                    .userMessage(userMessage)
                    .messages(List.of(userMessage))
                    .build();

            assertThat(session.getToolProvider().provideTools(request).aiServiceTools())
                    .extracting(tool -> tool.name())
                    .containsExactly("browser_navigate");
            assertThat(authorizationHeader.get()).isEqualTo("Bearer test-token");
        } finally {
            server.stop(0);
        }
    }

    private void handleMcpRequest(
            HttpExchange exchange,
            AtomicReference<String> authorizationHeader) throws IOException {
        authorizationHeader.set(exchange.getRequestHeaders().getFirst("Authorization"));
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Matcher idMatcher = REQUEST_ID_PATTERN.matcher(requestBody);
        String id = idMatcher.find() ? idMatcher.group(1) : "null";
        String responseBody;
        if (requestBody.contains("\"initialize\"")) {
            responseBody = """
                    {"jsonrpc":"2.0","id":%s,"result":{"protocolVersion":"2025-11-25","capabilities":{"tools":{}},"serverInfo":{"name":"browser-mcp-test","version":"1.0"}}}
                    """.formatted(id);
            exchange.getResponseHeaders().add("Mcp-Session-Id", "test-session");
        } else if (requestBody.contains("\"tools/list\"")) {
            responseBody = """
                    {"jsonrpc":"2.0","id":%s,"result":{"tools":[{"name":"browser_navigate","description":"打开网页","inputSchema":{"type":"object","properties":{}}}]}}
                    """.formatted(id);
        } else {
            responseBody = "{}";
        }

        byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
        exchange.close();
    }
}
