package com.youneng.troy.template.web.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xdf.pscommon.log4j2.core.LogManager;
import com.xdf.pscommon.log4j2.interfaces.Logger;
import com.xdf.seal.openfeign.support.SealOpenFeignHeader;
import com.youneng.seal.api.BaseStatusEnum;
import com.youneng.seal.api.resp.ObjectResults;
import com.youneng.tiger.common.env.TigerContextEnv;
import java.io.IOException;
import java.net.URLDecoder;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 验签的filter
 */
@Component
@WebFilter(urlPatterns = "/", filterName = "contextInjectFilter")
@Order(Integer.MIN_VALUE)
public class ContextInjectFilter implements Filter {

    /**
     * header中的email
     */
    private static final String email = "email";

    /**
     * header中的name
     */
    private static final String name = "name";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String userEmail = request.getHeader(email);
        String userName = request.getHeader(name);
        SealOpenFeignHeader.putHeader(email, userEmail);
        SealOpenFeignHeader.putHeader(name, userName);
        TigerContextEnv.setContextEnv(TigerContextEnv.USER_EMAIL, userEmail);
        if (StringUtils.isNotBlank(userName)) {
            TigerContextEnv.setContextEnv(TigerContextEnv.USER_NAME, URLDecoder.decode(userName, "UTF-8"));
        }

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            e.printStackTrace();
            dueException(response, e.getMessage(), BaseStatusEnum.ERROR.getStatus());
        } finally {
            TigerContextEnv.clean();
            SealOpenFeignHeader.clear();
        }

    }

    private void dueException(HttpServletResponse response, String error, int status) {
        response.setStatus(200);
        response.setHeader("Content-type", "application/json;charset=UTF-8");

        try {
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(new ObjectResults<>(status, error, null)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {

    }
}
