package com.weread.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String phone;
    private String password; // 仅用于密码登录
    private String vertificationcode;
}
