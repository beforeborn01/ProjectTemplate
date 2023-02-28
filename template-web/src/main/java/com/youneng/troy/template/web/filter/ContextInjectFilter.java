package com.youneng.troy.template.web.filter;

import com.youneng.troy.template.web.util.ContextEnv;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 上下文注入
 *
 * @author sunjianzhi
 * @date 2022-10-11
 */
@Component
@WebFilter(urlPatterns = "/", filterName = "contextInjectFilter")
@Order(Integer.MIN_VALUE)
@Slf4j
public class ContextInjectFilter implements Filter {

    private static final String EMAIL = "email";

    private static final String NAME = "name";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String userEmail = request.getHeader(EMAIL);
        String userName = request.getHeader(NAME);
        ContextEnv.setContextEnv(ContextEnv.USER_EMAIL, userEmail);
        if (StringUtils.isNotBlank(userName)) {
            ContextEnv.setContextEnv(ContextEnv.USER_NAME, URLDecoder.decode(userName, UTF_8.name()));
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            handleException(response, e.getMessage());
        } finally {
            ContextEnv.clean();
        }

    }

    private void handleException(HttpServletResponse response, String error) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        try {
            response.getWriter().write(error);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
