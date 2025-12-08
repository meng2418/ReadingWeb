package com.weread.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    // Access Token 过期时间：默认 7 天
    @Value("${jwt.expiration:604800000}") 
    private long expiration; 

    // Refresh Token 过期时间：默认 30 天
    @Value("${jwt.refreshExpiration:2592000000}") // 30 days in milliseconds
    private long refreshExpiration;

    @Value("${jwt.secret:default-secret-key-that-is-very-long-and-secure-and-must-be-changed-in-production}")
    private String secret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 【内部使用】创建不同过期时间的基础 JWT Token。
     * @param subject Token的主体（通常是手机号）
     * @param userId 用户ID
     * @param claims 附加信息
     * @param expirationMillis Token过期时间（毫秒）
     * @return JWT String
     */
    private String createToken(String subject, Long userId, Map<String, Object> claims, long expirationMillis) {
        if (claims == null) {
            claims = new HashMap<>();
        }
        claims.put("userId", userId);
        
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * 【对外接口】生成 Access Token 和 Refresh Token，并封装为 TokenInfo。
     * @param phone 手机号
     * @param userId 用户ID
     * @return 包含双 Token 和过期时间的 TokenInfo
     */
    public TokenInfo generateTokenInfo(String phone, Long userId) {
        
        // 1. 生成 Access Token (包含类型标记)
        Map<String, Object> accessClaims = new HashMap<>();
        accessClaims.put("type", "ACCESS");
        String accessToken = createToken(phone, userId, accessClaims, expiration);

        // 2. 生成 Refresh Token (包含类型标记)
        Map<String, Object> refreshClaims = new HashMap<>();
        refreshClaims.put("type", "REFRESH");
        String refreshToken = createToken(phone, userId, refreshClaims, refreshExpiration);

        // 3. 计算 AccessToken 的有效期（秒）
        Long expiresInSeconds = TimeUnit.MILLISECONDS.toSeconds(expiration);

        return new TokenInfo(accessToken, refreshToken, expiresInSeconds);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 Token 中提取手机号 (作为 Subject)
     */
    public String extractPhone(String token) {
        return extractAllClaims(token).getSubject();
    }
    
    /**
     * 检查 Token 是否过期
     */
    private Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String phone = extractPhone(token);
        // 1. 手机号是否匹配 2. Token 是否过期
        return (phone.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}