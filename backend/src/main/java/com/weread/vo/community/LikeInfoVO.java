package com.weread.vo.community; // 注意：通常 VO 会放在 vo 包下

import com.weread.vo.user.UserSummaryVO; 
import lombok.Data;

import java.util.List;

/**
 * 点赞信息响应值对象 (用于 GET /posts/{postId}/likes 等接口)
 */
@Data
public class LikeInfoVO {
    
    /**
     * 点赞目标（帖子或评论）的总点赞数
     */
    private long totalLikes;

    /**
     * 当前登录用户是否已点赞
     */
    private boolean isLiked;

    /**
     * 点赞该目标的部分用户的简要信息列表
     */
    private List<UserSummaryVO> likedUsers;
}