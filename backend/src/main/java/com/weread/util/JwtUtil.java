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

@Component
public class JwtUtil {

    @Value("${jwt.secret:default-secret-key-that-is-very-long-and-secure-and-must-be-changed-in-production}")
    private String secret;

    @Value("${jwt.expiration:604800000}") // 7 days in milliseconds
    private long expiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String phone, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("phone", phone);
        claims.put("userId", userId);
        
        Date now = new Date();
        Date validity = new Date(now.getTime() + expiration);

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(phone)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
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
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String phone = extractPhone(token);
        // 1. 手机号是否匹配 2. Token 是否过期
        return (phone.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> upstream/main
