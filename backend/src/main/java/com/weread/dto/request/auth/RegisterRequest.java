package com.weread.dto.request.auth;

import lombok.Data;
@Data
public class RegisterRequest {
    private String username;
    private String phone;
    private String verificationCode;
    private String password;
    private String confirmPassword;
}
