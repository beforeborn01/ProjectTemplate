package com.bc.template.web.filter;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
@WebFilter(urlPatterns = "/", filterName = "traceIdInjectFilter")
public class TraceIdInjectFilter implements Filter {

    public static final String TRACE_ID = "traceId";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        try {
            String traceId = getTraceId();
            MDC.put(TRACE_ID, traceId);
            filterChain.doFilter(servletRequest, servletResponse);
            response.setHeader(TRACE_ID, traceId);
        } finally {
            MDC.remove(TRACE_ID);
        }
    }

    private String getTraceId() {
        String traceId = TraceContext.traceId();
        if (traceId == null || "".equalsIgnoreCase(traceId)) {
            traceId = UUID.randomUUID().toString().replaceAll("-", "");
        }
        return traceId;
    }

    @Override
    public void destroy() {

    }
}
