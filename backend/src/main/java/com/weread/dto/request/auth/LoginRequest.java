package com.weread.dto.request.auth;

import lombok.Data;
@Data
public class LoginRequest {
    private String phone;
    private String type; // password | verificationCode
    private String password;
    private String verificationCode;
}
