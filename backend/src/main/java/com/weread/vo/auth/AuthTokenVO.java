package com.weread.vo.auth;

import lombok.Data;

@Data
public class AuthTokenVO {
    private String accessToken;   // 访问凭证
    private String refreshToken;  // 刷新凭证
    private Long expiresIn;       // 有效期（秒）
}
