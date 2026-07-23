package com.aicommerce.starter.aiChat.tool.browser;

import com.aicommerce.starter.aiChat.config.BrowserAutomationProperties;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BrowserAccessPolicyTest {

    @Test
    void shouldAllowConfiguredHostAndWildcardSubdomain() {
        BrowserAutomationProperties properties = new BrowserAutomationProperties();
        properties.setAllowedHosts(List.of("example.com", "*.example.org"));
        properties.setAllowPrivateNetwork(true);
        BrowserAccessPolicy policy = new BrowserAccessPolicy(properties);

        assertThatCode(() -> policy.validateNavigation("https://example.com/products"))
                .doesNotThrowAnyException();
        assertThatCode(() -> policy.validateNavigation("https://shop.example.org/products"))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldRejectHostOutsideAllowlist() {
        BrowserAutomationProperties properties = new BrowserAutomationProperties();
        properties.setAllowedHosts(List.of("example.com"));
        properties.setAllowPrivateNetwork(true);
        BrowserAccessPolicy policy = new BrowserAccessPolicy(properties);

        assertThatThrownBy(() -> policy.validateNavigation("https://example.org"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不允许访问主机");
    }

    @Test
    void shouldRejectUnsafeSchemeAndCredentials() {
        BrowserAutomationProperties properties = new BrowserAutomationProperties();
        properties.setAllowPrivateNetwork(true);
        BrowserAccessPolicy policy = new BrowserAccessPolicy(properties);

        assertThatThrownBy(() -> policy.validateNavigation("file:///etc/passwd"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTTP 或 HTTPS");
        assertThatThrownBy(() -> policy.validateNavigation("https://user:secret@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("用户名或密码");
    }

    @Test
    void shouldRejectLoopbackAndPrivateAddressesByDefault() {
        BrowserAccessPolicy policy = new BrowserAccessPolicy(new BrowserAutomationProperties());

        assertThatThrownBy(() -> policy.validateNavigation("http://127.0.0.1:8080"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("本机或私有网络");
        assertThatThrownBy(() -> policy.validateNavigation("http://localhost:8080"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("本机或私有网络");
    }

    @Test
    void shouldAllowBrowserInternalUrlsOnlyForSubrequests() {
        BrowserAccessPolicy policy = new BrowserAccessPolicy(new BrowserAutomationProperties());

        assertThatCode(() -> policy.validateRequest("data:text/plain,hello"))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> policy.validateNavigation("data:text/plain,hello"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTTP 或 HTTPS");
    }
}
