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
public class  JwtUtil {

    @Value("${jwt.expiration:604800000}") // 7天
    private long expiration;

    @Value("${jwt.refreshExpiration:2592000000}") // 30天
    private long refreshExpiration;

    @Value("${jwt.secret:}")
    private String secret;

    private Key signingKey;

    // 初始化 key
    private Key getSigningKey() {
        if (signingKey == null) {
            if (secret != null && secret.getBytes().length >= 32) {
                // 配置 secret 足够长，使用配置
                signingKey = Keys.hmacShaKeyFor(secret.getBytes());
            } else {
                // secret 太短或为空，自动生成一个安全 key
                signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
                System.out.println("⚠️ JWT secret 太短，已自动生成安全 key（仅开发环境）");
            }
        }
        return signingKey;
    }

    private String createToken(String subject, Integer userId, Map<String, Object> claims, long expirationMillis) {
        if (claims == null) claims = new HashMap<>();
        claims.put("userId", userId);
        Date now = new Date();
        Date valid = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(valid)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public TokenInfo generateTokenInfo(String phone, Integer userId) {
        // Access token
        Map<String, Object> accessClaims = new HashMap<>();
        accessClaims.put("type", "ACCESS");
        String accessToken = createToken(phone, userId, accessClaims, expiration);

        // Refresh token
        Map<String, Object> refreshClaims = new HashMap<>();
        refreshClaims.put("type", "REFRESH");
        String refreshToken = createToken(phone, userId, refreshClaims, refreshExpiration);

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

    public String extractPhone(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String phone = extractPhone(token);
        return phone.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public Integer getUserIdFromToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("userId", Integer.class);
        } catch (Exception e) {
            return null;
        }
    }
}
