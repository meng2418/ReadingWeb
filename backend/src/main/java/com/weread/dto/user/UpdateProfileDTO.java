package com.weread.dto.user;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Pattern;

@Data
public class UpdateProfileDTO {
    
    @Length(min = 2, max = 20, message = "用户名长度必须在2-20个字符之间")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\-_]+$", message = "用户名只能包含中文、英文、数字、下划线和减号")
    private String username;
    
    private String avatar;
    
    @Length(max = 200, message = "个人简介不能超过200个字符")
    private String bio;
}