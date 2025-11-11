package com.weread.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "aliyun.sms")
@Data
public class SmsConfig {
    // 对应 application.yml/properties 中的配置
    private String regionId;
    private String accessKeyId;
    private String accessKeySecret;
    private String signName;       // 您的短信签名
    private String templateCode;   // 您的验证码模板CODE，例如：SMS_123456789
}