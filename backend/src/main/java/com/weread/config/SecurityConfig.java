package com.weread.config;

import com.weread.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CorsConfig corsConfig;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, CorsConfig corsConfig) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.corsConfig = corsConfig;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                                // 1. 放行 OPTIONS 预检请求
                                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                                // 2. 认证相关接口（不需要token）
                                .requestMatchers("/auth/**").permitAll()

                                // 3. ⭐ 测试接口：公开访问（根据你的接口文档添加）
                                // 获取作者详情
                                .requestMatchers("GET", "/authors/**").permitAll()
                                // 获取书详情
                                .requestMatchers("GET", "/books/**").permitAll()
                                // 获取用户书评（如果是公开的）
                                .requestMatchers("GET", "/book-reviews/**").permitAll()
                                // 获取作者代表作
                                .requestMatchers("GET", "/authors/*/works").permitAll()
                                // 获取相关推荐作品
                                .requestMatchers("GET", "/books/*/recommendations").permitAll()
                                // 阅读器相关GET请求
                                .requestMatchers("GET", "/reader/**").permitAll()

                                // 4. ⭐ 需要认证的接口（后续测试时再开启）
                                // .requestMatchers("POST", "/book-reviews/**").authenticated()
                                // .requestMatchers("PUT", "/books/**/mark-finished").authenticated()

                                // 5. ⭐ 临时：其他所有请求也放行（测试阶段用）
                                .anyRequest().permitAll()

                        // ⭐ 测试完成后恢复这行（注释掉上面那行）：
                        // .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}