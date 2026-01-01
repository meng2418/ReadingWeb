package com.weread.dto.response.auth;
import com.weread.vo.user.UserSimpleVO;
import lombok.Data;

@Data
public class RegisterResponse {
    private UserSimpleVO user;
    private String token;
}
