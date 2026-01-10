package com.weread.filter;

import java.io.IOException;

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
        
        final String requestURI = request.getRequestURI();
        log.info("=== JWT过滤器开始 ===");
        log.info("请求URI: {}", requestURI);
        
        // ⭐ 关键修改：跳过不需要认证的路径
        if (shouldSkipAuthentication(requestURI)) {
            log.info("跳过认证的路径: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 放行 OPTIONS 请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.info("OPTIONS 请求，直接放行");
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        log.info("Authorization 头: {}", authHeader);

        // 检查 Authorization 头
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("缺少或格式错误的 Authorization 头");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":401,\"message\":\"未授权访问\"}");
            return;  // ⭐ 关键：这里要return，不能继续执行
        }

        final String jwt = authHeader.substring(7);
        log.info("JWT Token: {}...", jwt.length() > 20 ? jwt.substring(0, 20) : jwt);
        
        final String phone;
        
        try {
            // 从 Token 中提取手机号
            phone = jwtUtil.extractPhone(jwt);
            log.info("从Token提取的手机号: {}", phone);
        } catch (Exception e) {
            log.error("Token 解析失败: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":401,\"message\":\"Token无效\"}");
            return;
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
                            userEntity.getUserId(),  
                            null, 
                            userDetails.getAuthorities()
                        );
                    
                    authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // 设置到安全上下文
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("✅ 认证成功！UserEntity ID: {}", userEntity.getUserId());
                    log.info("SecurityContext Principal 类型: {}",
                        SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass().getName());
                    
                } else {
                    log.warn("Token 验证失败");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"code\":401,\"message\":\"Token验证失败\"}");
                    return;
                }
                
            } catch (Exception e) {
                log.error("加载用户失败: {}", e.getMessage());
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"code\":401,\"message\":\"用户不存在\"}");
                return;
            }
        } else {
            log.info("手机号为空或用户已认证");
        }
        
        filterChain.doFilter(request, response);
        log.info("=== JWT过滤器结束 ===");
    }
    
    // ⭐ 新增方法：判断是否需要跳过认证
    private boolean shouldSkipAuthentication(String requestURI) {
        // 这些路径不需要JWT认证
        return requestURI.startsWith("/auth/") || 
               requestURI.equals("/error") ||
               requestURI.equals("/") ||
               requestURI.startsWith("/swagger") ||
               requestURI.startsWith("/v3/api-docs") ||
               requestURI.startsWith("/webjars") ||
               requestURI.startsWith("/actuator");
    }
}