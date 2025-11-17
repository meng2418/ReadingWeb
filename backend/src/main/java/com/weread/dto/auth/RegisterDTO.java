package com.weread.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
// import com.weread.validation.FieldsValueMatch; // 假设使用的自定义注解

@Data
// @FieldsValueMatch(
//     field = "password", 
//     fieldMatch = "confirmPassword", 
//     message = "两次输入的密码不一致"
// ) 
public class RegisterDTO {
    
    // -----------------------------------------------------
    // 用户名 (根据您的要求新增)
    // 长度需在2-20位之间
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度需在2-20位之间")
    private String username;

    // -----------------------------------------------------
    // 手机号 (必填 + 格式校验)
    // 注意：您使用了 @NotNull，但通常 @NotBlank 更适合 String 字段 (检查非空且非空白)
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", 
             message = "手机号格式不正确")
    private String phone;
    
    // -----------------------------------------------------
    // 密码 (至少8位，且包含字母和数字)
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码长度不能少于8位 (已覆盖您说的6位)")
    // 正则表达式：(?=.*[a-zA-Z]) 必须包含字母，(?=.*[0-9]) 必须包含数字，.{8,} 长度至少8位
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,}$", 
             message = "密码必须包含字母和数字")
    private String password;
    
    // -----------------------------------------------------
    // 确认密码 (必填，用于和 password 字段进行比对)
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
