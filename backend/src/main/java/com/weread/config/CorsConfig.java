package com.weread.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    // 关键修改：用 List<String> 直接接收 YAML 中的列表，无需拆分
    @Value("${cors.allowed-origins:}") // 冒号后为空，避免配置缺失时报错
    private List<String> allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 直接设置允许的源（YAML 列表自动注入）
        configuration.setAllowedOrigins(allowedOrigins);

        // 允许的请求方法（保持不变）
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 允许的请求头（保持不变）
        configuration.addAllowedHeader("*");

        // 允许携带凭证（保持不变）
        configuration.setAllowCredentials(true);

        // 预检请求缓存时间（新增，优化跨域性能）
        configuration.setMaxAge(3600L);

        // 应用到所有 /api 前缀的接口（保持不变）
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }
}