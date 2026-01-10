package com.weread.service.community;

import com.weread.dto.community.LikeResponseDTO;

import java.util.Map;

import com.weread.dto.community.LikeDetailResponseDTO;
import com.weread.dto.community.LikeRequestDTO;

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
    
    /**
     * 点赞/取消点赞
     */
    Map<String, Object> toggleLike(LikeRequestDTO request, Integer userId);

}