package com.aicommerce.starter.aiChat.tool.browser;

import com.aicommerce.starter.aiChat.config.BrowserAutomationProperties;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.ServiceWorkerPolicy;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Paths;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提供给 AI 模型的 Playwright 浏览器自动化工具。
 *
 * <p>每个实例只服务一轮聊天，并通过同步方法保证 Playwright 对象不会被并发调用。</p>
 */
@Slf4j
public class PlaywrightBrowserTool implements AutoCloseable {

    private static final Pattern ELEMENT_REFERENCE_PATTERN = Pattern.compile("^\\[?(e\\d+)]?$");
    private static final String INTERACTIVE_ELEMENTS_SCRIPT = """
            elements => {
              document.querySelectorAll('[data-ai-ref]').forEach(element => element.removeAttribute('data-ai-ref'));
              return elements
                .filter(element => {
                  const style = window.getComputedStyle(element);
                  const rect = element.getBoundingClientRect();
                  return style.visibility !== 'hidden' && style.display !== 'none'
                    && rect.width > 0 && rect.height > 0;
                })
                .slice(0, 120)
                .map((element, index) => {
                  const ref = `e${index + 1}`;
                  element.setAttribute('data-ai-ref', ref);
                  const tag = element.tagName.toLowerCase();
                  const type = element.getAttribute('type');
                  const labels = element.labels
                    ? Array.from(element.labels).map(label => label.innerText).join(' ')
                    : '';
                  const rawName = element.getAttribute('aria-label')
                    || labels
                    || element.getAttribute('placeholder')
                    || element.innerText
                    || (type === 'password' ? '(password value hidden)' : element.value)
                    || element.getAttribute('title')
                    || element.getAttribute('name')
                    || '';
                  const name = String(rawName).replace(/\\s+/g, ' ').trim().slice(0, 160);
                  const role = element.getAttribute('role');
                  const state = element.disabled ? ' disabled' : '';
                  const descriptor = [tag, type ? `type=${type}` : '', role ? `role=${role}` : '']
                    .filter(Boolean)
                    .join(' ');
                  return `[${ref}] <${descriptor}> ${JSON.stringify(name)}${state}`;
                })
                .join('\\n');
            }
            """;

    private final BrowserAutomationProperties properties;
    private final BrowserAccessPolicy accessPolicy;

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    public PlaywrightBrowserTool(
            BrowserAutomationProperties properties,
            BrowserAccessPolicy accessPolicy) {
        this.properties = properties;
        this.accessPolicy = accessPolicy;
    }

    @Tool(name = "browserNavigate", value = "使用浏览器打开 HTTP 或 HTTPS 网页，并返回页面快照")
    public synchronized String navigate(@P("要打开的完整 URL") String url) {
        return execute("打开网页", () -> {
            accessPolicy.validateNavigation(url);
            currentPage().navigate(url.trim());
            return snapshotPage("网页已打开");
        });
    }

    @Tool(name = "browserSnapshot", value = "读取当前网页的 URL、标题、可交互元素引用和无障碍树")
    public synchronized String snapshot() {
        return execute("读取页面", () -> snapshotPage("当前页面"));
    }

    @Tool(name = "browserClick", value = "点击网页元素。优先使用页面快照中的 e1 形式引用，也支持 css=、text=、label=、placeholder= 前缀")
    public synchronized String click(@P("元素引用或定位表达式") String target) {
        return execute("点击元素", () -> {
            int pageCount = context.pages().size();
            resolveLocator(target).click();
            activateNewPage(pageCount);
            return snapshotPage("点击完成");
        });
    }

    @Tool(name = "browserFill", value = "清空并填写输入框。优先使用页面快照中的元素引用")
    public synchronized String fill(
            @P("输入框引用或定位表达式") String target,
            @P("要填写的文本") String value) {
        return execute("填写输入框", () -> {
            resolveLocator(target).fill(value == null ? "" : value);
            return snapshotPage("填写完成");
        });
    }

    @Tool(name = "browserPress", value = "在指定元素上按键，例如 Enter、Tab、Escape 或 Control+A")
    public synchronized String press(
            @P("元素引用或定位表达式") String target,
            @P("Playwright 按键名称") String key) {
        return execute("键盘操作", () -> {
            int pageCount = context.pages().size();
            resolveLocator(target).press(key);
            activateNewPage(pageCount);
            return snapshotPage("按键完成");
        });
    }

    @Tool(name = "browserSelectOption", value = "按照 option 的 value 选择下拉框选项")
    public synchronized String selectOption(
            @P("下拉框引用或定位表达式") String target,
            @P("option 的 value") String value) {
        return execute("选择下拉选项", () -> {
            resolveLocator(target).selectOption(value);
            return snapshotPage("选择完成");
        });
    }

    @Tool(name = "browserHover", value = "将鼠标悬停在指定元素上，用于展开菜单或提示")
    public synchronized String hover(@P("元素引用或定位表达式") String target) {
        return execute("悬停元素", () -> {
            resolveLocator(target).hover();
            return snapshotPage("悬停完成");
        });
    }

    @Tool(name = "browserGetText", value = "读取指定网页元素的可见文本")
    public synchronized String getText(@P("元素引用或定位表达式") String target) {
        return execute("读取元素文本", () -> truncate(resolveLocator(target).innerText()));
    }

    @Tool(name = "browserGoBack", value = "返回浏览器历史记录中的上一页")
    public synchronized String goBack() {
        return execute("返回上一页", () -> {
            currentPage().goBack();
            return snapshotPage("已返回上一页");
        });
    }

    @Tool(name = "browserGoForward", value = "前进到浏览器历史记录中的下一页")
    public synchronized String goForward() {
        return execute("前进下一页", () -> {
            currentPage().goForward();
            return snapshotPage("已前进下一页");
        });
    }

    @Tool(name = "browserListPages", value = "列出当前浏览器上下文内的全部标签页及其序号")
    public synchronized String listPages() {
        return execute("列出标签页", () -> {
            List<Page> pages = context.pages();
            StringBuilder result = new StringBuilder();
            for (int index = 0; index < pages.size(); index++) {
                Page current = pages.get(index);
                result.append(index)
                        .append(": ")
                        .append(current.title())
                        .append(" - ")
                        .append(current.url())
                        .append('\n');
            }
            return truncate(result.toString());
        });
    }

    @Tool(name = "browserSwitchPage", value = "切换到 browserListPages 返回的指定序号标签页")
    public synchronized String switchPage(@P("标签页序号，从 0 开始") int pageIndex) {
        return execute("切换标签页", () -> {
            List<Page> pages = context.pages();
            if (pageIndex < 0 || pageIndex >= pages.size()) {
                throw new IllegalArgumentException("标签页序号超出范围: " + pageIndex);
            }
            page = pages.get(pageIndex);
            page.bringToFront();
            return snapshotPage("标签页已切换");
        });
    }

    private String execute(String operation, Supplier<String> action) {
        try {
            ensureBrowserStarted();
            return action.get();
        } catch (RuntimeException exception) {
            log.warn("Playwright浏览器工具执行失败，operation={}", operation, exception);
            return operation + "失败: " + safeMessage(exception);
        }
    }

    private void ensureBrowserStarted() {
        if (playwright != null) {
            return;
        }

        playwright = Playwright.create();
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(properties.isHeadless())
                .setTimeout(properties.getTimeoutMillis());
        if (properties.getExecutablePath() != null && !properties.getExecutablePath().isBlank()) {
            launchOptions.setExecutablePath(Paths.get(properties.getExecutablePath().trim()));
        }

        browser = playwright.chromium().launch(launchOptions);
        context = browser.newContext(new Browser.NewContextOptions()
                .setAcceptDownloads(false)
                .setServiceWorkers(ServiceWorkerPolicy.BLOCK));
        context.setDefaultTimeout(properties.getTimeoutMillis());
        context.setDefaultNavigationTimeout(properties.getTimeoutMillis());
        context.route("**/*", this::handleRoute);
        page = context.newPage();
    }

    private void handleRoute(Route route) {
        try {
            accessPolicy.validateRequest(route.request().url());
            route.resume();
        } catch (RuntimeException exception) {
            log.debug("已拦截浏览器网络请求: {}", route.request().url());
            route.abort("blockedbyclient");
        }
    }

    private Page currentPage() {
        activateOpenPage();
        if (page == null || page.isClosed()) {
            page = context.newPage();
        }
        return page;
    }

    private void activateOpenPage() {
        if (context == null) {
            return;
        }
        List<Page> pages = context.pages().stream()
                .filter(candidate -> !candidate.isClosed())
                .toList();
        if (pages.isEmpty()) {
            page = context.newPage();
        } else if (page == null || page.isClosed()) {
            page = pages.get(pages.size() - 1);
        }
    }

    private void activateNewPage(int previousPageCount) {
        List<Page> pages = context.pages().stream()
                .filter(candidate -> !candidate.isClosed())
                .toList();
        if (pages.size() > previousPageCount) {
            page = pages.get(pages.size() - 1);
        }
    }

    private Locator resolveLocator(String target) {
        if (target == null || target.isBlank()) {
            throw new IllegalArgumentException("元素引用不能为空");
        }

        String expression = target.trim();
        Matcher referenceMatcher = ELEMENT_REFERENCE_PATTERN.matcher(expression);
        Locator locator;
        if (referenceMatcher.matches()) {
            locator = currentPage().locator("[data-ai-ref=\"" + referenceMatcher.group(1) + "\"]");
        } else if (expression.startsWith("css=")) {
            locator = currentPage().locator(expression.substring(4));
        } else if (expression.startsWith("text=")) {
            locator = currentPage().getByText(expression.substring(5));
        } else if (expression.startsWith("label=")) {
            locator = currentPage().getByLabel(expression.substring(6));
        } else if (expression.startsWith("placeholder=")) {
            locator = currentPage().getByPlaceholder(expression.substring(12));
        } else {
            locator = currentPage().locator(expression);
        }

        int count = locator.count();
        if (count == 0) {
            throw new IllegalArgumentException("没有找到元素: " + expression + "，请重新读取页面快照");
        }
        return locator.first();
    }

    private String snapshotPage(String message) {
        Page current = currentPage();
        String interactiveElements = (String) current.locator(
                        "a[href],button,input:not([type=hidden]),textarea,select,summary,"
                                + "[role=button],[role=link],[role=checkbox],[role=radio],[role=tab],"
                                + "[role=menuitem],[role=option],[contenteditable=true]")
                .evaluateAll(INTERACTIVE_ELEMENTS_SCRIPT);
        String accessibilitySnapshot = current.ariaSnapshot();

        String result = message
                + "\nURL: " + current.url()
                + "\n标题: " + current.title()
                + "\n\n可交互元素:\n"
                + (interactiveElements == null || interactiveElements.isBlank() ? "(无)" : interactiveElements)
                + "\n\n无障碍树:\n"
                + (accessibilitySnapshot == null || accessibilitySnapshot.isBlank()
                ? "(页面没有可读取内容)"
                : accessibilitySnapshot);
        return truncate(result);
    }

    private String truncate(String value) {
        if (value == null) {
            return "";
        }
        int maxOutputChars = properties.getMaxOutputChars();
        if (value.length() <= maxOutputChars) {
            return value;
        }
        return value.substring(0, maxOutputChars) + "\n...(内容已截断，请使用 browserGetText 精确读取)";
    }

    private String safeMessage(RuntimeException exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            return exception.getClass().getSimpleName();
        }
        int lineBreak = message.indexOf('\n');
        return lineBreak < 0 ? message : message.substring(0, lineBreak);
    }

    @Override
    public synchronized void close() {
        closeQuietly(context);
        closeQuietly(browser);
        closeQuietly(playwright);
        context = null;
        browser = null;
        playwright = null;
        page = null;
    }

    private void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception exception) {
            log.debug("关闭Playwright资源失败: {}", exception.getMessage());
        }
    }
}
