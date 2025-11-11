package com.weread.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {
    private String username;
    @NotNull(message = "手机号不能为空")
    private String phone;
    @Size(min = 6, message = "密码至少6位")
    private String password;
    private String confirmPassword;
}
