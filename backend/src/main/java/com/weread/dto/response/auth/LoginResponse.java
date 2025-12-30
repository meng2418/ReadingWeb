package com.weread.dto.response.auth;
import com.weread.vo.user.UserSimpleVO;
import lombok.Data;

@Data
public class LoginResponse {
    private UserSimpleVO user;
    private String token;
}

