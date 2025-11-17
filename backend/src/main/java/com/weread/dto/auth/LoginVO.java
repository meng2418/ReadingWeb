package com.weread.dto.auth;

import lombok.Data;

@Data
public class LoginVO {
    // 凭证信息
    private AuthTokenVO token; 
    
    // 用户信息
    private UserBasicVO user;
}
