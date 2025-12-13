package com.weread.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginDTO {
    
    // 手机号校验
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^(13\\d|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18\\d|19[0-35-9])\\d{8}$", 
             message = "手机号格式不正确")
    private String phone;
    
    // 密码或验证码，取决于登录方式
    private String password; 
    private String sms;     
}
