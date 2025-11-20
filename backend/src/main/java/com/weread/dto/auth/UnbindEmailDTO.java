package com.weread.dto.auth;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * UnbindAccountDTO - 解除绑定手机号或邮箱的请求体
 * 需通过验证码确认身份。
 */
@Data
public class UnbindEmailDTO {
    private String email; 

    @NotBlank(message = "验证码不能为空")
    private String vertificationCode;
}
