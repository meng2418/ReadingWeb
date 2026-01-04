package com.weread.service.community;

import com.weread.dto.community.CommentDTO;
import com.weread.dto.community.CommentResponseDTO;
import com.weread.dto.community.CreateCommentRequestDTO;
import com.weread.dto.community.ReplyResponseDTO;
import org.springframework.data.domain.Page;


public interface CommentService {
    
    // 获取评论列表
    Page<CommentDTO> getCommentsByPostId(Integer postId, int page, int limit, Integer currentUserId);
    
    // 发布评论
    CommentResponseDTO createComment(Integer postId, CreateCommentRequestDTO request, Integer userId);
    
    // 回复评论
    ReplyResponseDTO replyComment(Integer commentId, CreateCommentRequestDTO request, Integer userId);
    
    // 获取评论详情
    CommentDTO getCommentById(Integer commentId, Integer currentUserId);
}