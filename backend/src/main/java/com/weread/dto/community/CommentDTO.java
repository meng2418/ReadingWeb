package com.weread.dto.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Integer commentId;
    private Integer userId;
    private String username;
    private String avatar;
    private String content;
    private LocalDateTime commentTime;
    private String replyToUsername;
    private Integer parentCommentId;
    private Integer likeCount;
    private Boolean isLiked;
    private List<CommentDTO> replies; // 子评论

    // 确保在转换时处理 null 值
    public String getReplyToUsername() {
        return replyToUsername != null ? replyToUsername : "";
    }
}