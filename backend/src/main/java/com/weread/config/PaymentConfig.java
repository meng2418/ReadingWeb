package com.weread.config;

import com.weread.payment.client.WechatPayClient;
import com.weread.payment.client.impl.FakeWechatPayClient;
import com.weread.payment.client.AlipayClient;
import com.weread.payment.client.FakeAlipayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {

    @Bean
    public WechatPayClient wechatPayClient() {
        return new FakeWechatPayClient();
    }

    @Bean
    public AlipayClient alipayClient() {
        return new FakeAlipayClient();
    }
}
