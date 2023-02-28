package com.youneng.troy.template.web;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.youneng.troy.template.service.util.DingTalkAlertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.youneng.troy")
@EnableTransactionManagement
@EnableFeignClients(basePackages = "com.youneng.troy")
@EnableMethodCache(basePackages = "com.youneng.troy.template.web")
@Slf4j
public class ApplicationStarter {

    public static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(ApplicationStarter.class, args);
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            log.error("UncaughtExceptionHandler thread=" + t.getName(), e);
            DingTalkAlertUtil dingTalkAlertUtil = applicationContext.getBean("dingTalkAlertUtil",
                DingTalkAlertUtil.class);
            dingTalkAlertUtil.alert(null,"","default exception handler");
        });
    }

}
