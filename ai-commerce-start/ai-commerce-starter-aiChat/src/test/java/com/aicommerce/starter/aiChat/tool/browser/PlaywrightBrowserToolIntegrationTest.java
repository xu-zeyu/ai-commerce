package com.aicommerce.starter.aiChat.tool.browser;

import com.aicommerce.starter.aiChat.config.BrowserAutomationProperties;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfEnvironmentVariable(named = "PLAYWRIGHT_CHROMIUM_EXECUTABLE_PATH", matches = ".+")
class PlaywrightBrowserToolIntegrationTest {

    private HttpServer httpServer;

    @AfterEach
    void tearDown() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    @Test
    void shouldNavigateFillClickAndReadText() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        httpServer.createContext("/", exchange -> {
            byte[] body = """
                    <!doctype html>
                    <html lang="zh-CN">
                    <head><title>浏览器工具测试</title></head>
                    <body>
                      <label for="name">姓名</label>
                      <input id="name" />
                      <button onclick="document.querySelector('#result').textContent = '你好，' + document.querySelector('#name').value">提交</button>
                      <div id="result"></div>
                    </body>
                    </html>
                    """.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        httpServer.start();

        BrowserAutomationProperties properties = new BrowserAutomationProperties();
        properties.setAllowPrivateNetwork(true);
        properties.setExecutablePath(System.getenv("PLAYWRIGHT_CHROMIUM_EXECUTABLE_PATH"));

        try (PlaywrightBrowserTool browserTool =
                     new PlaywrightBrowserTool(properties, new BrowserAccessPolicy(properties))) {
            String snapshot = browserTool.navigate(
                    "http://127.0.0.1:" + httpServer.getAddress().getPort());

            assertThat(snapshot)
                    .contains("浏览器工具测试")
                    .contains("姓名")
                    .contains("提交");
            assertThat(browserTool.fill("label=姓名", "Codex")).contains("填写完成");
            assertThat(browserTool.click("text=提交")).contains("点击完成");
            assertThat(browserTool.getText("css=#result")).isEqualTo("你好，Codex");
        }
    }
}
