package com.youneng.troy.template.web.filter;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xdf.pscommon.log4j2.core.LogManager;
import com.xdf.pscommon.log4j2.interfaces.Logger;
import com.xdf.seal.openfeign.support.SealOpenFeignHeader;
import com.youneng.seal.api.BaseStatusEnum;
import com.youneng.seal.api.resp.ObjectResults;
import com.youneng.troy.template.web.util.ProjectTemplateContextEnv;
import java.io.IOException;
import java.net.URLDecoder;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * 上下文注入
 *
 * @author sunjianzhi
 * @date 2022-10-11
 */
@Component
@WebFilter(urlPatterns = "/", filterName = "contextInjectFilter")
@Order(Integer.MIN_VALUE)
public class ContextInjectFilter implements Filter {

    public static final Logger LOGGER = LogManager.getLogger(ContextInjectFilter.class);

    private static final String EMAIL = "email";

    private static final String NAME = "name";


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
        FilterChain filterChain) throws IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String userEmail = request.getHeader(EMAIL);
        String userName = request.getHeader(NAME);
        SealOpenFeignHeader.putHeader(EMAIL, userEmail);
        SealOpenFeignHeader.putHeader(NAME, userName);
        ProjectTemplateContextEnv.setContextEnv(ProjectTemplateContextEnv.USER_EMAIL, userEmail);
        if (StringUtils.isNotBlank(userName)) {
            ProjectTemplateContextEnv
                .setContextEnv(ProjectTemplateContextEnv.USER_NAME, URLDecoder.decode(userName,
                    UTF_8.name()));
        }

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            handleException(response, e.getMessage(), BaseStatusEnum.ERROR.getStatus());
        } finally {
            ProjectTemplateContextEnv.clean();
            SealOpenFeignHeader.clear();
        }

    }

    private void handleException(HttpServletResponse response, String error, int status) {
        response.setStatus(HttpStatus.OK.value());
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        try {
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter()
                .write(mapper.writeValueAsString(new ObjectResults<>(status, error, null)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
