package com.aicommerce.starter.aiChat.tool.browser;

import com.aicommerce.starter.aiChat.config.BrowserAutomationProperties;
import org.springframework.stereotype.Component;

import java.net.IDN;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;

/**
 * 浏览器网络访问策略，限制协议、主机白名单和私有网络访问。
 */
@Component
public class BrowserAccessPolicy {

    private final BrowserAutomationProperties properties;

    public BrowserAccessPolicy(BrowserAutomationProperties properties) {
        this.properties = properties;
    }

    public void validateNavigation(String url) {
        validateHttpUrl(url);
    }

    public void validateRequest(String url) {
        URI uri = parseUri(url);
        String scheme = normalize(uri.getScheme());
        if ("about".equals(scheme) || "data".equals(scheme) || "blob".equals(scheme)) {
            return;
        }
        validateHttpUrl(uri);
    }

    private void validateHttpUrl(String url) {
        validateHttpUrl(parseUri(url));
    }

    private void validateHttpUrl(URI uri) {
        String scheme = normalize(uri.getScheme());
        if (!"http".equals(scheme) && !"https".equals(scheme)) {
            throw new IllegalArgumentException("浏览器只允许访问 HTTP 或 HTTPS 地址");
        }
        if (uri.getUserInfo() != null) {
            throw new IllegalArgumentException("浏览器地址不能包含用户名或密码");
        }

        String host = normalizeHost(uri.getHost());
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("浏览器地址缺少有效主机名");
        }
        validateAllowedHost(host);
        validatePublicAddress(host);
    }

    private URI parseUri(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("浏览器地址不能为空");
        }
        try {
            return URI.create(url.trim());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("浏览器地址格式无效", exception);
        }
    }

    private void validateAllowedHost(String host) {
        List<String> allowedHosts = properties.getAllowedHosts();
        if (allowedHosts == null || allowedHosts.stream().allMatch(this::isBlank)) {
            return;
        }

        boolean allowed = allowedHosts.stream()
                .filter(value -> !isBlank(value))
                .map(this::normalizeHost)
                .anyMatch(pattern -> matchesHost(host, pattern));
        if (!allowed) {
            throw new IllegalArgumentException("浏览器不允许访问主机: " + host);
        }
    }

    private boolean matchesHost(String host, String pattern) {
        if (pattern == null) {
            return false;
        }
        if (pattern.startsWith("*.")) {
            String suffix = pattern.substring(1);
            return host.endsWith(suffix) && host.length() > suffix.length();
        }
        return host.equals(pattern);
    }

    private void validatePublicAddress(String host) {
        if (properties.isAllowPrivateNetwork()) {
            return;
        }
        if ("localhost".equals(host) || host.endsWith(".localhost")) {
            throw new IllegalArgumentException("浏览器禁止访问本机或私有网络地址");
        }

        try {
            for (InetAddress address : InetAddress.getAllByName(host)) {
                if (!isPublicAddress(address)) {
                    throw new IllegalArgumentException("浏览器禁止访问本机或私有网络地址");
                }
            }
        } catch (UnknownHostException exception) {
            throw new IllegalArgumentException("浏览器无法解析主机: " + host, exception);
        }
    }

    private boolean isPublicAddress(InetAddress address) {
        if (address.isAnyLocalAddress()
                || address.isLoopbackAddress()
                || address.isLinkLocalAddress()
                || address.isSiteLocalAddress()
                || address.isMulticastAddress()) {
            return false;
        }
        if (address instanceof Inet4Address) {
            return isPublicIpv4(address.getAddress());
        }
        if (address instanceof Inet6Address) {
            byte firstByte = address.getAddress()[0];
            return (firstByte & 0xFE) != 0xFC;
        }
        return false;
    }

    private boolean isPublicIpv4(byte[] address) {
        int first = Byte.toUnsignedInt(address[0]);
        int second = Byte.toUnsignedInt(address[1]);
        int third = Byte.toUnsignedInt(address[2]);

        if (first == 0 || first == 10 || first == 127 || first >= 224) {
            return false;
        }
        if (first == 100 && second >= 64 && second <= 127) {
            return false;
        }
        if (first == 169 && second == 254) {
            return false;
        }
        if (first == 172 && second >= 16 && second <= 31) {
            return false;
        }
        if (first == 192 && second == 168) {
            return false;
        }
        if (first == 192 && second == 0 && (third == 0 || third == 2)) {
            return false;
        }
        if (first == 198 && (second == 18 || second == 19)) {
            return false;
        }
        if (first == 198 && second == 51 && third == 100) {
            return false;
        }
        return !(first == 203 && second == 0 && third == 113);
    }

    private String normalizeHost(String host) {
        if (host == null) {
            return null;
        }
        String normalized = host.trim().toLowerCase(Locale.ROOT);
        if (normalized.startsWith("[") && normalized.endsWith("]")) {
            normalized = normalized.substring(1, normalized.length() - 1);
        }
        try {
            return IDN.toASCII(normalized);
        } catch (IllegalArgumentException exception) {
            return normalized;
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.toLowerCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
