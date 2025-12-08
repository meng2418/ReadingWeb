package com.weread.dto.community;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class TopicCreationDTO {

    @NotBlank(message = "话题名称不能为空")
    @Size(min = 2, max = 50, message = "话题名称长度需在2到50个字符之间")
    private String name;

    @Size(max = 255, message = "话题描述不能超过255个字符")
    private String description;
}