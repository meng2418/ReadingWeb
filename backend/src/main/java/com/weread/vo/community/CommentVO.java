package com.weread.vo.community;

import com.weread.vo.user.UserSummaryVO;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentVO {
    private Integer commentId;
    private Integer postId;
    private Integer parentCommentId; 
    
    private UserSummaryVO author; 
    private String content;
    private int likesCount; 
    private boolean isLiked; 
    private LocalDateTime createdAt;

    private List<CommentVO> replies; 
    private int replyCount; 
}