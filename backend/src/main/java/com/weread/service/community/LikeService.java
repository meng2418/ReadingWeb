package com.weread.service.community;

import com.weread.dto.community.LikeResponseDTO;
import com.weread.dto.community.LikeDetailResponseDTO;

public interface LikeService {
    
    // 帖子点赞/取消点赞
    LikeResponseDTO togglePostLike(Integer postId, Integer userId);
    
    // 评论点赞/取消点赞
    LikeResponseDTO toggleCommentLike(Integer commentId, Integer userId);
    
    // 获取帖子点赞详情（仅作者可见）
    LikeDetailResponseDTO getPostLikeDetails(Integer postId, Integer userId);
    
    // 检查是否点赞
    Boolean isPostLiked(Integer postId, Integer userId);
    Boolean isCommentLiked(Integer commentId, Integer userId);
}