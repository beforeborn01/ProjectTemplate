package com.youneng.troy.template.web.util;

import java.net.InetAddress;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerMapping;

import com.xdf.pscommon.alert.DingtalkAlert;
import com.xdf.pscommon.log4j2.core.LogManager;
import com.xdf.pscommon.log4j2.interfaces.Logger;

/**
 * @author sunjianzhi
 */
@RefreshScope
@Configuration
public class DingTalkAlertUtils {

    public static final Logger LOGGER = LogManager.getLogger(DingTalkAlertUtils.class);

    @Value("${spring.cloud.config.profile}")
    private String env;
    @Value("${basealert.dingtalk.enable:true}")
    private boolean dingTalkEnable;

    /**
     * 发送request请求异常的钉钉报警
     */
    public void dingTalkAlert(HttpServletRequest req, Exception e) {
        if (!dingTalkEnable) {
            return;
        }
        try {
            Object urlObject = req.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            Object url = urlObject == null ? req.getRequestURL() : urlObject;
            String params = RequestJsonUtil.getRequestJsonString(req);
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            DingtalkAlert.get("product").alertMarkdown("", null, "template异常信息",
                Arrays.asList("【环境】 : 【 " + env + " 】", "IP : " + hostAddress, "traceId : " + TraceContext.traceId(), "URL : " + url,
                    "params : " + params, "user : " + ProjectTemplateContextEnv.getUserEmail()),
                "https://kibanalb.staff.xdf.cn/s/youneng-a-pro/app/kibana#/discover?_g=()", e);
        } catch (Exception exception) {
            LOGGER.error("钉钉报警异常", e);
        }
    }
}
