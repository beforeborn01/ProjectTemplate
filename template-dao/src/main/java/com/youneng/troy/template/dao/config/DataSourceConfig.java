package com.youneng.troy.template.dao.config;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.xdf.pscommon.log4j2.core.LogManager;
import com.xdf.pscommon.log4j2.interfaces.Logger;

/**
 * 数据库源配置
 *
 */
@Configuration
@MapperScan(basePackages = "com.youneng.troy.template.dao.mapper", sqlSessionTemplateRef = "sqlSessionTemplate")
public class DataSourceConfig {

    public static final Logger logger = LogManager.getLogger(DataSourceConfig.class);

    @Value("${mysql.tiger.mapperLocations}")
    private String mapperLocations;

    // 加载全局的配置文件
    @Value("${mysql.configLocation}")
    private String configLocation;

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("routeDataSource") DataSource dataSource) {
        logger.info("--------------------  sqlSessionFactory init ---------------------");
        try {
            SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
            sessionFactoryBean.setDataSource(dataSource);

            // 设置mapper.xml文件所在位置
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(mapperLocations);
            sessionFactoryBean.setMapperLocations(resources);
            // 设置mybatis-config.xml配置文件位置
            sessionFactoryBean.setConfigLocation(new DefaultResourceLoader().getResource(configLocation));

            return sessionFactoryBean.getObject();
        } catch (IOException e) {
            logger.error("mybatis resolver mapper*xml is error", e);
        } catch (Exception e) {
            logger.error("mybatis sqlSessionFactoryBean create error", e);
        }
        return null;
    }
}
