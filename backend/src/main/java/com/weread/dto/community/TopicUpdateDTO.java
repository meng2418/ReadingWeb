package com.weread.dto.community;

import lombok.Data;

import jakarta.validation.constraints.Size;

@Data
public class TopicUpdateDTO {

    // 名称通常是唯一的，如果允许修改，也需要校验唯一性
    @Size(min = 2, max = 50, message = "话题名称长度需在2到50个字符之间")
    private String name; 

    @Size(max = 255, message = "话题描述不能超过255个字符")
    private String description;
    
    // 允许部分字段更新，所以没有 @NotBlank
}