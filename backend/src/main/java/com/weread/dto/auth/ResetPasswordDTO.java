package com.weread.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 接口 2: 重置密码接口的请求体
 * 包含手机号、验证码和新密码及其确认
 */
@Data
public class ResetPasswordDTO {
    
    // 手机号码
    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确") 
    private String phoneNumber;
    
    // 验证码
    @NotBlank(message = "验证码不能为空")
    @Size(min = 4, max = 6, message = "验证码长度为4-6位") 
    private String smsCode;

    // 新密码（用于重置）
    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, message = "密码长度至少为8位") 
    private String newPassword;
    
    // 确认新密码（用于确保两次输入一致）
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}