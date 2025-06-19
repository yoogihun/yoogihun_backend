package com.ygh.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ygh.api", "com.ygh.infra","com.ygh.account","com.ygh.transaction","com.ygh.common"})
@EntityScan(basePackages = {"com.ygh.account.domain.account.entity", "com.ygh.transaction.domain.transaction.entity"})
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
