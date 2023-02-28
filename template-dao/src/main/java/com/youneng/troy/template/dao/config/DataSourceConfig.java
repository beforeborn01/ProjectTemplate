package com.youneng.troy.template.dao.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Function;


/**
 * 数据库源配置
 */
@Configuration
@MapperScan(basePackages = "com.youneng.troy.template.dao.mapper")
@Slf4j
@Component
public class DataSourceConfig {

    @Value("${mysql.tiger.mapperLocations}")
    private String mapperLocations;

    // 加载全局的配置文件
    @Value("${mysql.configLocation}")
    private String configLocation;

    @Autowired
    private DbConfig db;

    @Bean
    public DataSource dataSource() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.configFromPropety(db);
        dataSource.init();
        return dataSource;
    }

    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) {
        log.info("--------------------  sqlSessionFactory init ---------------------");
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
            log.error("mybatis resolver mapper*xml is error", e);
        } catch (Exception e) {
            log.error("mybatis sqlSessionFactoryBean create error", e);
        }
        return null;
    }

//    @Bean(name = "sqlSessionTemplate")
//    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }

    @Data
    public static class DruidConfig extends Properties {

        private static final String PRE_DRUID = "druid.";

        @Override
        public synchronized Object computeIfAbsent(Object key, Function<? super Object, ?> mappingFunction) {

            String newKey = PRE_DRUID + key;

            return super.computeIfAbsent(newKey, mappingFunction);
        }
    }
}
