package com.weread.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI weReadOpenAPI() {
        // 1. Configure JWT Security Scheme
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("bearerAuth");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        // 2. Configure API Meta Information
        Info info = new Info()
                .title("WeRead Backend API")
                .version("1.0.0")
                .description("API documentation for the WeRead backend system.")
                .contact(new Contact()
                        .name("WeRead Dev Team")
                        .email("dev@example.com"));

        // 3. Assemble OpenAPI Configuration
        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", securityScheme));
    }
}