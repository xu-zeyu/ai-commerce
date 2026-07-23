# aiChat Browser MCP 浏览器工具

`ai-commerce-starter-aiChat` 通过 LangChain4j MCP 客户端连接独立部署的 Browser MCP 服务，并将服务端提供的工具动态注册到 AI Services。

业务应用不再包含 Playwright Java 依赖、Chromium 或浏览器安装脚本。浏览器运行时及其网络访问策略由 Browser MCP 服务单独管理。

## 接入方式

Browser MCP 服务需要提供 Streamable HTTP 端点。配置服务地址后启用：

```bash
export AI_CHAT_BROWSER_MCP_ENABLED=true
export AI_CHAT_BROWSER_MCP_ENDPOINT=http://browser-mcp:8931/mcp
```

若 MCP 服务需要 Bearer Token：

```bash
export AI_CHAT_BROWSER_MCP_BEARER_TOKEN=replace-with-secret
```

可选配置：

```bash
export AI_CHAT_BROWSER_MCP_TIMEOUT_MILLIS=60000
export AI_CHAT_BROWSER_MCP_MAX_TOOL_ROUND_TRIPS=12
```

完整配置位于 `jinHan-shop/jinHan-shop-admin-web/src/main/resources/application.yml` 的 `ai.chat.browser-mcp` 节点。

## 会话隔离

每轮聊天会创建独立的 MCP 客户端会话，并在响应完成、失败、超时或 SSE 断开时关闭。Browser MCP 服务应根据 MCP 会话创建隔离的浏览器上下文，避免不同用户共享页面、Cookie 或登录状态。

## 部署要求

- Browser MCP 服务与管理后台应用分开部署，Chromium 等浏览器依赖只安装在 MCP 服务侧。
- 生产环境建议使用 HTTPS 或仅在受控内部网络开放 MCP 端点。
- 建议启用 Bearer Token，并由 Browser MCP 服务限制可访问的域名、下载和文件系统能力。
- 业务应用默认关闭 Browser MCP；未配置端点时不会尝试建立连接。
