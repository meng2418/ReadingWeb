package com.weread.dto.community;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Data
public class PostCreationDTO {
    
    @NotBlank(message = "标题不能为空")
    @Size(min = 1, max = 100, message = "标题长度必须在1-100字符之间")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;
    
    // 书本ID列表（可以为空）
    private List<Integer> bookIds;

    private List<String> topics;

    
}