package com.youneng.troy.template.service.util;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import com.xdf.pscommon.alert.DingtalkAlert;
import com.xdf.pscommon.log4j2.core.LogManager;
import com.xdf.pscommon.log4j2.interfaces.Logger;

/**
 * @author sunjianzhi
 */
@RefreshScope
@Configuration
public class DingTalkAlertUtil {

    public static final Logger LOGGER = LogManager.getLogger(DingTalkAlertUtil.class);

    @Value("${spring.cloud.config.profile}")
    private String ENV;
    @Value("${basealert.dingtalk.enable:true}")
    private boolean dingTalkEnable;

    public void alert(Exception e,String user,String customMessage) {
        if (!dingTalkEnable) {
            return;
        }
        try {
            List<String> contents = Arrays.asList("【环境】 : 【 " + ENV + " 】",
                "IP : " + InetAddress.getLocalHost().getHostAddress(),
                "traceId : " + TraceContext.traceId(),
                "user : " + user,
                "customMessage : "+customMessage);
            DingtalkAlert.get(ENV).alertMarkdown("", null, "template异常信息", contents,
                "https://kibanalb.staff.xdf.cn/s/youneng-a-pro/app/kibana#/discover?_g=()", e);
        } catch (Exception exception) {
            LOGGER.error("钉钉报警异常", e);
        }
    }

}
