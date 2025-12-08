package com.weread.vo.auth;

import com.weread.vo.user.UserDetailVO;
import lombok.Data;

@Data
public class LoginVO {
    // 凭证信息
    private AuthTokenVO token; 
    
    // 用户信息
    private UserDetailVO user;
}
