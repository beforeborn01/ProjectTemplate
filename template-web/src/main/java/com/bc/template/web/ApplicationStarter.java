package com.bc.template.web;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.bc.template.service.util.FeishuAlertUtil;
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
@ComponentScan("com.bc")
@EnableTransactionManagement
@EnableFeignClients(basePackages = "com.bc")
@EnableMethodCache(basePackages = "com.bc.template.web")
@Slf4j
public class ApplicationStarter {

    public static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(ApplicationStarter.class, args);
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            log.error("UncaughtExceptionHandler thread=" + t.getName(), e);
            FeishuAlertUtil feishuAlertUtil = applicationContext.getBean("feishuAlertUtil",
                FeishuAlertUtil.class);
            feishuAlertUtil.alert(null, "", "default exception handler");
        });
    }

}
