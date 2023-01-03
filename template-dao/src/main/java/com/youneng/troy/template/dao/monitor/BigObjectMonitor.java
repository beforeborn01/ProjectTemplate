package com.youneng.troy.template.dao.monitor;

import com.xdf.pscommon.alert.DingtalkAlert;
import java.util.Arrays;

import com.youneng.seal.plugins.bigobjectmonitor.BigObjectInfo;
import com.youneng.seal.plugins.notice.SealObserver;
import com.youneng.seal.plugins.utils.BigObjectConvertUtil;

public class BigObjectMonitor implements SealObserver {
    /**
     * 环境变量
     */
    private final static String ENV = System.getProperty("spring.cloud.config.profile");

    @Override
    public void notice(Object arg) {
        BigObjectInfo bigObjectInfo = (BigObjectInfo) arg;
        // 填充SpringBoot的配置信息_以SpringBoot的为准
        bigObjectInfo.setEnvironment(ENV);
        bigObjectInfo.setProjectName("ProjectTemplate");
        String message = BigObjectConvertUtil.makeUpMessage((BigObjectInfo)arg);
        DingtalkAlert.get(ENV).alertText(null, Arrays.asList(""), Arrays.asList(message));
    }


}
