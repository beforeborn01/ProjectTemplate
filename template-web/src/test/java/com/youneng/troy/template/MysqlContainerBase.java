package com.youneng.troy.template;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: tdd-table-copy
 * @Package com.youneng.troy.table.copy
 * @Description:
 * @date Date : 2022年06月20日 14:04
 */
@Testcontainers
public class MysqlContainerBase {

    public static MySQLContainer mysql;

    /**
     * 使用这种方式初始化可以做到mysql的复用，而使用@Container注解的方式每个类都会重新初始化mysql容器。根据需求选择
     */
    static {
        if (mysql == null) {
            mysql = (MySQLContainer)new MySQLContainer(DockerImageName.parse("mysql:5.7")).withDatabaseName("demo").withInitScript("table.sql") // 指定数据库初始化脚本，默认的db为test
                .withPassword("admin").withUsername("admin"); // 指定登录信息，不指定也可以。指定后可以通过外部工具直接连接mysql进行查看
            mysql.start();
        }
    }

    /**
     * spring-framework 5.2.5针对TestContainers做了友好适配，引入了@DynamicPropertySource注解， 从而可以更方便地修改spring依赖的相关配置
     * 
     * @param registry
     */
    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("seal.route.data-source-config-map.masterDatasource.url", mysql::getJdbcUrl);// 使用容器对象获取jdbcUrl，会自动设置合适的ip和端口号
        registry.add("seal.route.data-source-config-map.masterDatasource.username", mysql::getUsername);
        registry.add("seal.route.data-source-config-map.masterDatasource.password", mysql::getPassword);
    }
    
}
