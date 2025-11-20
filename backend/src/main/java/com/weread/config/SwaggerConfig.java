package com.weread.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 配置类
 * 用于生成交互式接口文档，兼容现有安全、缓存等配置
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI weReadOpenAPI() {
        // 1. 配置 JWT 认证（与 SecurityConfig 中的认证逻辑对齐）
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("bearerAuth");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        // 2. 配置文档基本信息
        Info info = new Info()
                .title("微信读书网页版 API")
                .version("1.0.0")
                .description("微信读书网页版完整接口文档，包含认证、书架、书籍、阅读器等模块")
                .contact(new Contact()
                        .name("开发团队")
                        .email("dev@example.com"));

        // 3. 组装 OpenAPI 配置
        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", securityScheme));
    }
}