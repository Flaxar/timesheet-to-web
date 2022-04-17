package com.example.timesheet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfigProperties {

    // public static String hsmProvider;
    public static String datasourceURL;
    public static String datasourceUsername;
    public static String datasourcePassword;

    @Autowired
    public AppConfigProperties(
        @Value("${spring.datasource.url}") String datasourceURL,
        @Value("${spring.datasource.username}") String datasourceUsername,
        @Value("${spring.datasource.password}") String datasourcePassword
    ) {
        AppConfigProperties.datasourceURL = datasourceURL;
        AppConfigProperties.datasourceUsername = datasourceUsername;
        AppConfigProperties.datasourcePassword = datasourcePassword;
    }
}