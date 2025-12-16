package com.weread;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 微信读书网页版项目启动类
 */
@EnableJpaAuditing
@SpringBootApplication
public class ReadingWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReadingWebApplication.class, args);
    }
}