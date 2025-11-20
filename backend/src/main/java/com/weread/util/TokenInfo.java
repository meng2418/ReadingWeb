package com.weread.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * TokenInfo - 封装访问令牌、刷新令牌及其有效期（秒）
 * 用于JwtUtil和Controller之间的数据传输。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo {
    /** 访问令牌 */
    private String accessToken;
    
    /** 刷新令牌 */
    private String refreshToken;
    
    /** AccessToken 有效期（秒） */
    private Long expiresIn; 
}