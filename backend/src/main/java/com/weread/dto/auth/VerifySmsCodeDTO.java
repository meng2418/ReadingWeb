package com.weread.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 步骤 2: 验证码校验或用于流程中的中间校验
 * 包含手机号和验证码
 */
@Data
public class VerifySmsCodeDTO {

    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确") 
    private String phoneNumber;

    @NotBlank(message = "验证码不能为空")
    @Size(min = 4, max = 6, message = "验证码长度为4-6位") 
    private String smsCode;
}