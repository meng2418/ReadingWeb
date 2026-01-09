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
        
        log.info("=== JWT过滤器开始 ===");
        log.info("请求URI: {}", request.getRequestURI());
        
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
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        log.info("JWT Token: {}...", jwt.length() > 20 ? jwt.substring(0, 20) : jwt);
        
        // 修改这里：声明为 final 或 effectively final
        final String phone;  // 添加 final
        
        try {
            // 从 Token 中提取手机号
            phone = jwtUtil.extractPhone(jwt);  // 直接赋值
            log.info("从Token提取的手机号: {}", phone);
        } catch (Exception e) {
            log.error("Token 解析失败: {}", e.getMessage());
            filterChain.doFilter(request, response);
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
                            userEntity,  // 改为 UserEntity
                            null, 
                            userDetails.getAuthorities()
                        );
                    
                    authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // 设置到安全上下文
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    // 设置 userId 到请求属性，供控制器使用
                    Integer userId = userEntity.getUserId();
                    request.setAttribute("userId", userId);
                    log.info("✅ 认证成功！UserEntity ID: {}", userId);
                    log.info("SecurityContext Principal 类型: {}",
                        SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass().getName());
                    
                } else {
                    log.warn("Token 验证失败");
                }
                
            } catch (Exception e) {
                log.error("加载用户失败: {}", e.getMessage());
                e.printStackTrace();
            }
        } else {
            log.info("手机号为空或用户已认证");
        }
        
        filterChain.doFilter(request, response);
        log.info("=== JWT过滤器结束 ===");
    }
}