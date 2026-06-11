package com.aicommerce.common.log;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class TraceIdFilter implements Filter {

    public static final String TRACE_ID_HEADER = "X-Trace-Id"; // 请求头获取 Trace ID 的 header
                                                               // 名称

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        // 尝试从请求头获取 Trace ID
        try {
            String traceId = ((HttpServletRequest) servletRequest).getHeader(TRACE_ID_HEADER);

            // 如果请求头没有，则生成新的 Trace ID
            if (traceId == null || traceId.isEmpty()) {
                traceId = ApplicationTraceIdHelper.generate();
            }

            // 将 Trace ID 放入 MDC
            ApplicationTraceIdHelper.setTraceId(traceId);

            // 将 Trace ID 放入响应头，方便链路追踪
            ((HttpServletResponse) servletResponse).setHeader(TRACE_ID_HEADER, traceId);
            filterChain.doFilter(servletRequest, servletResponse);
        }
        finally {
            ApplicationTraceIdHelper.removeTraceId();
        }
    }

}