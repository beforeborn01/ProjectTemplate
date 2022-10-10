package com.youneng.troy.template.web.util;

import com.xdf.pscommon.alert.DingtalkAlert;
import com.xdf.pscommon.log4j2.core.LogManager;
import com.xdf.pscommon.log4j2.interfaces.Logger;
import com.youneng.tiger.common.env.TigerContextEnv;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.Arrays;

@RefreshScope
@Configuration
public class DingtalkAlertUtils {

    public static final Logger logger = LogManager.getLogger(DingtalkAlertUtils.class);

    @Value("${spring.cloud.config.profile}")
    private String env;
    @Value("${basealert.dingtalk.enable}")
    private boolean dingtalkEnable;

    /**
     * 钉钉报警
     *
     * @param req
     * @param e
     */
    public void dingtalkAlert(HttpServletRequest req, Exception e) {

        if (!dingtalkEnable) {
            return;
        }

        try {
            Object urlObject = req.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            Object url = urlObject == null ? req.getRequestURL() : urlObject;
            String params = RequestJsonUtil.getRequestJsonStringNoException(req);
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            DingtalkAlert.get("product").alertMarkdown("", null, "TIGER异常信息",
                Arrays.asList("【环境】 : 【 " + env + " 】", "IP : " + hostAddress, "traceId : " + TraceContext.traceId(), "URL : " + url, "params : " + params, "user : " + ProjectTemplateContextEnv
                    .getUserEmail()),
                "https://kibanalb.staff.xdf.cn/s/youneng-a-pro/app/kibana#/discover?_g=()", e);
        } catch (Exception exception) {
            logger.error("钉钉报警异常", e);
        }
    }

    /**
     * 钉钉报警
     *
     * @param message
     * @param e
     */
    public void dingtalkAlertError(String message, Exception e) {

        if (!dingtalkEnable) {
            return;
        }
        try {
            DingtalkAlert.get("product").alertMarkdown("", null, "TIGER异常信息", Arrays.asList("【环境】 : 【 " + env + " 】", "traceId : " + TraceContext.traceId(), "message : " + message),
                "https://kibanalb.staff.xdf.cn/s/youneng-a-pro/app/kibana#/discover?_g=()", e);
        } catch (Exception exception) {
            logger.error("钉钉报警异常", e);
        }
    }
}
