package com.youneng.troy.template.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.xdf.pscommon.log4j2.core.LogManager;
import com.xdf.pscommon.log4j2.interfaces.Logger;
import com.youneng.troy.template.service.util.DingTalkAlertUtil;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.youneng.troy")
@EnableTransactionManagement
@EnableFeignClients(basePackages = "com.youneng.troy")
public class ApplicationStarter {

    public static final Logger LOGGER = LogManager.getLogger(ApplicationStarter.class);

    public static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(ApplicationStarter.class, args);
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            LOGGER.error("UncaughtExceptionHandler thread=" + t.getName(), e);
            DingTalkAlertUtil dingTalkAlertUtil = applicationContext.getBean("dingTalkAlertUtil",
                DingTalkAlertUtil.class);
            dingTalkAlertUtil.alert(null,"","default exception handler");
        });
    }

}
