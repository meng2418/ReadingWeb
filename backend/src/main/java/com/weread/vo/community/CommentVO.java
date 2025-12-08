package com.weread.vo.community;

import com.weread.vo.user.UserSummaryVO;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentVO {
    private Long commentId;
    private Long postId;
    private Long parentCommentId; 
    
    private UserSummaryVO author; 
    private String content;
    private long likesCount; 
    private boolean isLiked; 
    private LocalDateTime createdAt;

    private List<CommentVO> replies; 
    private int replyCount; 
}