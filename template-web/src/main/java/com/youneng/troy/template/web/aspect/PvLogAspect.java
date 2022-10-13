package com.youneng.troy.template.web.aspect;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.xdf.pscommon.log4j2.core.LogManager;
import com.xdf.pscommon.log4j2.interfaces.Logger;

import lombok.Data;

/**
 * Controller层 入参日志
 *
 * @author huyue
 */
@Aspect
@Component
@Import(PvLogAspect.PvLogConfig.class)
@Order(Integer.MIN_VALUE)
public class PvLogAspect {

    public static final Logger LOGGER = LogManager.getLogger(PvLogAspect.class);

    private static final JsonMapper MAPPER = new JsonMapper();

    @Resource
    private PvLogConfig pvLogConfig;

    @Value("${spring.application.name}")
    private String project;

    @Pointcut("execution(* com.youneng.troy.template.web.controller..*Controller.*(..))"
        + " || execution(* com.youneng.troy.template.web.apiimpl..*.*(..))"
        + " || execution(* com.youneng.troy.template.web.handler.GlobalRequestExceptionHandler.*(..))")
    public void pvLog() {}

    @Around("pvLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {

        StartPvInfo startPvInfo = null;

        if (needBeforePvLog(joinPoint)) {
            startPvInfo = before(joinPoint);
        }

        Object result = joinPoint.proceed();

        if (needAfterPvLog()) {
            after(startPvInfo, result);
        }

        return result;
    }

    /**
     * 方法开始执行前打印入参
     */
    public StartPvInfo before(JoinPoint joinPoint) {

        // 获取打印日志信息
        StartPvInfo startPvInfo = buildStartPvInfo(joinPoint);

        // pv日志打印
        LOGGER.pv(project, startPvInfo.getUri(), startPvInfo.getMethod(), "\n入参 ：" + startPvInfo.getParams(), startPvInfo.getUid(),
            " \n" + startPvInfo.getCustom());

        return startPvInfo;
    }

    /**
     * 方法执行后打印入参、返参
     */
    public void after(StartPvInfo startPvInfo, Object returnObj) {

        if (startPvInfo == null) {
            return;
        }
        // 获取打印日志信息
        EndPvInfo endPvInfo = buildAfterPvInfo(returnObj);

        String params = "\n入参 ：" + startPvInfo.getParams() + "\n 返参： " + endPvInfo.getResultStr();
        String custom = "\n" + startPvInfo.getCustom() + "\n 耗时：" + (endPvInfo.endTime - startPvInfo.startTime);

        // pv日志打印
        LOGGER.pv(project, startPvInfo.getUri(), startPvInfo.getMethod(), params, startPvInfo.getUid(), custom);
    }

    /**
     * 是否需要开始pv日志 去除被ControllerAdvice注解的类；忽略特定的url
     */
    private boolean needBeforePvLog(JoinPoint joinPoint) {
        try {
            Class<?> aClass = joinPoint.getTarget().getClass();
            ControllerAdvice controllerAdvice = aClass.getAnnotation(ControllerAdvice.class);
            if (controllerAdvice != null) {
                return false;
            }
            return needPvLog();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return true;
    }

    private boolean needAfterPvLog() {
        return needPvLog();
    }

    private boolean needPvLog() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
            if (Objects.isNull(attributes)) {
                return false;
            }
            HttpServletRequest httpServletRequest = attributes.getRequest();

            // OPTIONS 请求不需要记录
            String method = httpServletRequest.getMethod();
            if (method.equalsIgnoreCase(HttpMethod.OPTIONS.name())) {
                return false;
            }

            // 忽略不需要记录的url
            if (CollectionUtils.isEmpty(pvLogConfig.getFilterUrls())) {
                return true;
            }
            String url = httpServletRequest.getRequestURI();
            for (String filterUrl : pvLogConfig.getFilterUrls()) {
                if (url.contains(filterUrl)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    private StartPvInfo buildStartPvInfo(JoinPoint joinPoint) {
        StartPvInfo startPvInfo = new StartPvInfo();

        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(attributes)) {
            return startPvInfo;
        }

        HttpServletRequest httpServletRequest = attributes.getRequest();

        startPvInfo.setMethod(httpServletRequest.getMethod());

        startPvInfo.setUri(httpServletRequest.getRequestURI());

        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();

        Map<String, String> headerMap = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = httpServletRequest.getHeader(headerName);
            headerMap.put(headerName, headerValue);
        }
        // 设置header信息
        startPvInfo.setCustom(toJsonString(headerMap));
        // 设置用户信息
        startPvInfo.setUid(httpServletRequest.getHeader(pvLogConfig.getUuidName()));
        // 设置入参列表
        startPvInfo.setParams(toJsonString(joinPoint.getArgs()));

        return startPvInfo;
    }

    /**
     * 构造after pv日志需要的参数
     */
    private EndPvInfo buildAfterPvInfo(Object returnObj) {
        EndPvInfo endPvInfo = new EndPvInfo();

        String resultJson = toJsonString(returnObj);
        // 超长截取，避免耗时过长
        if (StringUtils.isNotBlank(resultJson) && resultJson.length() > pvLogConfig.getMaxResultLength()) {
            resultJson = resultJson.substring(0, pvLogConfig.getMaxResultLength());
        }
        // 返参列表
        endPvInfo.setResultStr(resultJson);

        return endPvInfo;
    }

    private String toJsonString(Object o) {
        try {
            return MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            LOGGER.error("to json error", e);
        }
        return null;
    }

    @Data
    private static class StartPvInfo {
        /**
         * 请求URI
         */
        private String uri;

        /**
         * 请求方法
         */
        private String method;

        /**
         * 请求参数
         */
        private String params;

        /**
         * 用户UID
         */
        private String uid;
        /**
         * 自定义信息
         */
        private String custom;

        /**
         * 请求开始时间
         */
        private Long startTime = System.currentTimeMillis();
    }

    @Data
    private static class EndPvInfo {
        /**
         * 请求结束时间
         */
        private Long endTime = System.currentTimeMillis();
        /**
         * 返回数据
         */
        private String resultStr;
    }

    @Data
    @RefreshScope
    @ConfigurationProperties("pv")
    public static class PvLogConfig {
        /**
         * 需要过滤掉的url
         */
        public List<String> filterUrls;
        /**
         * 最大的返回值长度 默认10240
         */
        public Integer maxResultLength = 10240;
        /**
         * header里去的用户id名 默认email
         */
        public String uuidName = "email";
    }
}
