package com.weread.dto.community;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LikeRequestDTO {
    
    @NotNull(message = "目标ID不能为空")
    private Integer targetId;
    
    @NotNull(message = "目标类型不能为空")
    private String targetType; // "post" 或 "comment"
    
    private Integer commentId; // 当 targetType="comment" 时有值
    private Integer postId;    // 当 targetType="comment" 时可选，targetType="post" 时必填
}