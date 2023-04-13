package com.bc.template.web.config;

import com.alicp.jetcache.CacheBuilder;
import com.alicp.jetcache.anno.support.GlobalCacheConfig;
import com.alicp.jetcache.anno.support.SpringConfigProvider;
import com.alicp.jetcache.event.CacheGetEvent;
import com.alicp.jetcache.event.CachePutEvent;
import com.alicp.jetcache.external.ExternalCacheBuilder;
import com.bc.template.web.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class JetCacheConfig {

    /**
     * 自定义SpringConfigProvider，使得缓存的放入和读取都可以打出日志
     */
    @Bean
    @Primary
    public SpringConfigProvider springConfigProvider(GlobalCacheConfig globalCacheConfig) {

        SpringConfigProvider springConfigProvider = new SpringConfigProvider();
        springConfigProvider.setGlobalCacheConfig(globalCacheConfig);

        Map<String, CacheBuilder> allCacheBuilders = new HashMap<>();
        allCacheBuilders.putAll(globalCacheConfig.getLocalCacheBuilders());
        allCacheBuilders.putAll(globalCacheConfig.getRemoteCacheBuilders());

        allCacheBuilders.forEach((key, value) -> {
            ExternalCacheBuilder externalCacheBuilder = (ExternalCacheBuilder)value;
            externalCacheBuilder.addMonitor(event -> {
                if (event instanceof CachePutEvent) {
                    CachePutEvent putEvent = (CachePutEvent)event;
                    log.info("添加缓存 " + putEvent.getResult().isSuccess() + " key = "
                            + JsonUtil.toJsonString(putEvent.getKey()) + " value="
                            + JsonUtil.toJsonString(putEvent.getValue()));
                } else if (event instanceof CacheGetEvent) {
                    CacheGetEvent getEvent = (CacheGetEvent)event;
                    log.info("获取缓存 " + getEvent.getResult().isSuccess() + " key = "
                            + JsonUtil.toJsonString(getEvent.getKey()) + " value="
                            + JsonUtil.toJsonString(getEvent.getResult().getValue()));
                }
            });
        });

        return springConfigProvider;
    }
}
