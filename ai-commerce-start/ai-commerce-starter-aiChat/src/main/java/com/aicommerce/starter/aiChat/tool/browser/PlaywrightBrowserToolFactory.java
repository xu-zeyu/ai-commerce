package com.aicommerce.starter.aiChat.tool.browser;

import com.aicommerce.starter.aiChat.config.BrowserAutomationProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 为单轮 AI 聊天创建隔离的浏览器工具。
 */
@Component
@Slf4j
public class PlaywrightBrowserToolFactory {

    private final BrowserAutomationProperties properties;
    private final BrowserAccessPolicy accessPolicy;

    public PlaywrightBrowserToolFactory(
            BrowserAutomationProperties properties,
            BrowserAccessPolicy accessPolicy) {
        this.properties = properties;
        this.accessPolicy = accessPolicy;
    }

    @PostConstruct
    public void logBrowserToolConfiguration() {
        log.info(
                "AI浏览器工具配置: enabled={}, headless={}, allowedHosts={}, allowPrivateNetwork={}",
                properties.isEnabled(),
                properties.isHeadless(),
                properties.getAllowedHosts(),
                properties.isAllowPrivateNetwork());
    }

    public boolean isEnabled() {
        return properties.isEnabled();
    }

    public int getMaxToolRoundTrips() {
        return properties.getMaxToolRoundTrips();
    }

    public PlaywrightBrowserTool createTool() {
        return new PlaywrightBrowserTool(properties, accessPolicy);
    }
}
