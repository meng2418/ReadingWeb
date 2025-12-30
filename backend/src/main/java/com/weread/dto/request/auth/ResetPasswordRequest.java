package com.weread.dto.request.auth;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String phone;
    private String verificationCode;
    private String newPassword;
    private String confirmPassword;
}
