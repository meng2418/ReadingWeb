package com.weread.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.weread.entity.user.UserEntity;
import com.weread.repository.user.UserRepository;
import com.weread.service.impl.user.UserDetailsServiceImpl;
import com.weread.util.JwtUtil;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   UserDetailsServiceImpl userDetailsService,
                                   UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String requestUri = request.getRequestURI();
        final String requestMethod = request.getMethod();

        log.info("=== JWT过滤器开始 ===");
        log.info("请求URI: {} {}", requestMethod, requestUri);

        // 1. 放行 OPTIONS 请求
        if ("OPTIONS".equalsIgnoreCase(requestMethod)) {
            log.info("OPTIONS 请求，直接放行");
            filterChain.doFilter(request, response);
            return;
        }

        // 2. ⭐ 检查是否应该跳过JWT验证（公开接口）
        if (shouldSkipAuthentication(requestUri, requestMethod)) {
            log.info("公开接口，跳过JWT验证: {} {}", requestMethod, requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 验证JWT Token（需要认证的接口）
        final String authHeader = request.getHeader("Authorization");
        log.info("Authorization 头: {}", authHeader);

        // 检查 Authorization 头
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("缺少或格式错误的 Authorization 头");
            // ⭐ 测试阶段：所有接口都放行
            filterChain.doFilter(request, response);
            return;

            /*
            // ⭐ 测试完成后恢复这部分的逻辑：
            if (requiresAuthentication(requestUri, requestMethod)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"code\":401,\"message\":\"未提供有效的认证信息\"}");
                return;
            } else {
                // 不需要认证的接口，直接放行
                filterChain.doFilter(request, response);
                return;
            }
            */
        }

        final String jwt = authHeader.substring(7);
        log.info("JWT Token: {}...", jwt.length() > 20 ? jwt.substring(0, 20) : jwt);

        // 修复lambda问题：使用局部变量捕获
        final String phone;  // 声明为final

        try {
            // 从 Token 中提取手机号
            phone = jwtUtil.extractPhone(jwt);  // 直接赋值给final变量
            log.info("从Token提取的手机号: {}", phone);
        } catch (Exception e) {
            log.error("Token 解析失败: {}", e.getMessage());
            // ⭐ 测试阶段：直接放行
            filterChain.doFilter(request, response);
            return;

            /*
            // ⭐ 测试完成后恢复：
            if (requiresAuthentication(requestUri, requestMethod)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"code\":401,\"message\":\"Token无效或已过期\"}");
                return;
            } else {
                filterChain.doFilter(request, response);
                return;
            }
            */
        }

        // 检查用户是否已认证
        if (phone != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 先加载 UserDetails 用于验证 Token
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(phone);
                log.info("加载用户详情成功: {}", phone);

                // 验证 Token
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    log.info("Token 验证成功");

                    // 获取 UserEntity
                    UserEntity userEntity = userRepository.findByPhone(phone)
                            .orElseThrow(() -> new RuntimeException("用户不存在: " + phone));

                    log.info("找到 UserEntity，ID: {}", userEntity.getUserId());

                    // 使用 UserEntity 作为 principal
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userEntity,  // 改为 UserEntity
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 设置到安全上下文
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("✅ 认证成功！UserEntity ID: {}", userEntity.getUserId());

                } else {
                    log.warn("Token 验证失败");
                    // ⭐ 测试阶段：直接放行
                    filterChain.doFilter(request, response);
                    return;
                }

            } catch (Exception e) {
                log.error("加载用户失败: {}", e.getMessage());
                // ⭐ 测试阶段：直接放行
                filterChain.doFilter(request, response);
                return;
            }
        } else {
            log.info("手机号为空或用户已认证");
        }

        filterChain.doFilter(request, response);
        log.info("=== JWT过滤器结束 ===");
    }

    /**
     * ⭐ 判断是否应该跳过JWT验证（公开接口）
     * 测试阶段：所有接口都跳过JWT验证
     */
    private boolean shouldSkipAuthentication(String uri, String method) {
        // ⭐ 测试阶段：所有接口都跳过JWT验证
        return true;

        /*
        // ⭐ 测试完成后恢复的逻辑：
        // 公开接口列表（可以根据需要扩展）
        List<String> publicGetUris = Arrays.asList(
            "/auth/",
            "/authors/",
            "/books/",
            "/book-reviews/",
            "/reader/"
        );

        List<String> publicPostUris = Arrays.asList(
            "/auth/"
        );

        // GET请求：检查是否为公开接口
        if ("GET".equalsIgnoreCase(method)) {
            return publicGetUris.stream().anyMatch(uri::startsWith);
        }

        // POST请求：检查是否为公开接口（如登录、注册）
        if ("POST".equalsIgnoreCase(method)) {
            return publicPostUris.stream().anyMatch(uri::startsWith);
        }

        return false;
        */
    }

    /**
     * ⭐ 判断接口是否需要认证
     * 测试阶段：都不需要认证
     */
    private boolean requiresAuthentication(String uri, String method) {
        // ⭐ 测试阶段：暂时都返回false（都不需要认证）
        return false;
    }
}