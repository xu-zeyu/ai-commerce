package com.aicommerce.starter.aiChat.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * Playwright 浏览器自动化配置。
 */
@Data
@Validated
@ConfigurationProperties(prefix = "ai.chat.browser")
public class BrowserAutomationProperties {

    /**
     * 是否向 AI 模型开放浏览器工具。
     */
    private boolean enabled = false;

    /**
     * 是否使用无头浏览器。
     */
    private boolean headless = true;

    /**
     * 浏览器操作和页面导航的超时时间。
     */
    @Min(1000)
    @Max(120000)
    private int timeoutMillis = 30000;

    /**
     * 单个浏览器工具结果允许返回给模型的最大字符数。
     */
    @Min(1000)
    @Max(50000)
    private int maxOutputChars = 12000;

    /**
     * 单轮聊天允许的最大工具往返次数。
     */
    @Min(1)
    @Max(30)
    private int maxToolRoundTrips = 12;

    /**
     * 允许访问的主机。空列表表示允许全部公网主机；支持 *.example.com。
     */
    private List<String> allowedHosts = new ArrayList<>();

    /**
     * 是否允许访问环回、链路本地和私有网段。默认关闭以防止 SSRF。
     */
    private boolean allowPrivateNetwork = false;

    /**
     * 可选的 Chromium 可执行文件路径。留空时使用 Playwright 安装的 Chromium。
     */
    private String executablePath;
}
