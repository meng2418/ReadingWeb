package com.weread.dto.community;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 评论创建请求数据传输对象
 */
@Data
public class CommentCreationDTO {
    
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容不能超过500字")
    private String content;

    /**
     * 父评论ID。如果为 null，则表示这是一级评论。
     * 如果有值，则表示是对某条评论的回复 (二级评论)。
     */
    private Long parentCommentId; 
}