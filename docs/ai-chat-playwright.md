# aiChat Playwright 浏览器工具

`ai-commerce-starter-aiChat` 已通过 LangChain4j AI Services 向模型提供 Playwright 浏览器工具。工具只在单轮请求内共享一个无痕 `BrowserContext`，请求完成、失败或 SSE 连接关闭后会自动释放浏览器资源。

## 本地启用

先安装与项目依赖版本匹配的 Chromium：

```bash
./gradlew :ai-commerce-start:ai-commerce-starter-aiChat:installPlaywrightChromium
```

浏览器工具默认启用。需要显式配置时可设置：

```bash
export AI_CHAT_BROWSER_ENABLED=true
export AI_CHAT_BROWSER_HEADLESS=true
```

运行时不会自动下载浏览器。在 macOS 和常见 Linux 路径下会自动发现系统 Chrome、Edge 或 Chromium；也可以通过 `AI_CHAT_BROWSER_EXECUTABLE_PATH` 显式指定。

生产镜像发布脚本会在 `admin-web` 镜像内自动安装 Chromium，无需另外执行安装任务。

## 网络访问范围

浏览器默认只允许 HTTP/HTTPS 公网地址，并拒绝环回、链路本地和私有网段。建议在生产环境配置主机白名单：

```bash
export AI_CHAT_BROWSER_ALLOWED_HOSTS=example.com,*.example.org
```

只有明确需要访问内网应用时，才设置：

```bash
export AI_CHAT_BROWSER_ALLOW_PRIVATE_NETWORK=true
```

## 可用工具

- `browserNavigate`：打开网页
- `browserSnapshot`：读取 URL、标题、元素引用和无障碍树
- `browserClick`、`browserFill`、`browserPress`、`browserSelectOption`、`browserHover`：操作页面元素
- `browserGetText`：读取指定元素文本
- `browserGoBack`、`browserGoForward`：浏览历史导航
- `browserListPages`、`browserSwitchPage`：管理标签页

页面快照会为可交互元素生成 `e1`、`e2` 等临时引用。页面变化后应重新获取快照，再继续操作。

完整配置位于 `jinHan-shop/jinHan-shop-admin-web/src/main/resources/application.yml` 的 `ai.chat.browser` 节点。
