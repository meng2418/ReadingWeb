package com.weread.dto.community;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class PublishPostRequestDTO {
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    private List<Integer> bookIds;
    private List<String> topics;
    private String publishLocation;
}