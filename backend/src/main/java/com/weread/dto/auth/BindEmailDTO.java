package com.weread.dto.auth;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BindEmailDTO {
    private String email;
    @NotBlank(message = "验证码不能为空")
    private String vertificationCode;
}
