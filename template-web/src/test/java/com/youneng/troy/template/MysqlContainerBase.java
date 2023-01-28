package com.youneng.troy.template;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
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
public class MysqlContainerBase implements BeforeAllCallback {

    public static MySQLContainer mysql;

    @Override
    public void beforeAll(ExtensionContext context) {
        if (mysql == null) {
            mysql = (MySQLContainer) new MySQLContainer(DockerImageName.parse("mysql:5.7")).withDatabaseName("demo").withInitScript("table.sql") // 指定数据库初始化脚本，默认的db为test
                    .withPassword("admin").withUsername("admin"); // 指定登录信息，不指定也可以。指定后可以通过外部工具直接连接mysql进行查看
            mysql.start();
            System.setProperty("jdbc.url", mysql.getJdbcUrl());// 使用容器对象获取jdbcUrl，会自动设置合适的ip和端口号
            System.setProperty("seal.route.data-source-config-map.masterDatasource.username", mysql.getUsername());
            System.setProperty("seal.route.data-source-config-map.masterDatasource.password", mysql.getPassword());
        }
    }
}
