package com.example.timesheet.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Component
@Configuration
public class DataSourceConfig {
    // this is used for the spring framework itself (credentials verification etc.)
   
    @Value("${spring.datasource.url}") private String datasourceURL;
    @Value("${spring.datasource.username}") private String datasourceUsername;
    @Value("${spring.datasource.password}") private String datasourcePassword;

    @Bean
    protected DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();

        // see the http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html
        dataSourceBuilder.url(datasourceURL);
        dataSourceBuilder.username(datasourceUsername);
        dataSourceBuilder.password(datasourcePassword);
        // System.out.println(datasourceURL);
        // System.out.println(datasourceUsername);
        // System.out.println(datasourcePassword);

        return dataSourceBuilder.build();
    }
}
