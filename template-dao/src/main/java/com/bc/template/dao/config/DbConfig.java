package com.bc.template.dao.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "db")
@Data
public class DbConfig extends Properties{

    private Properties db;

}
