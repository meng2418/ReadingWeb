package com.weread.controller;

import com.weread.dto.community.CommentCreationDTO;
import com.weread.vo.community.CommentListVO;
import com.weread.service.community.CommunityService;
import com.weread.vo.community.CommentVO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;
    
    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    /**
     * 【POST /posts/{postId}/comments】 发表评论 (支持一级评论和回复)
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentVO> createComment(
        @PathVariable Long postId,
        @RequestAttribute("userId") Long userId, // 从 JWT/Filter 中获取当前用户ID
        @RequestBody @Valid CommentCreationDTO dto) {
        
        CommentVO commentVO = communityService.createComment(postId, userId, dto);
        return new ResponseEntity<>(commentVO, HttpStatus.CREATED);
    }

    /**
     * 【GET /posts/{postId}/comments】 获取一级评论列表 (分页)
     */
    @GetMapping("/pesponseosts/{postId}/comments")
    public ResponseEntity<CommentListVO> getPostComments(
        @PathVariable Long postId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int limit,
        @RequestAttribute(value = "userId", required = false) Long currentUserId) { // currentUserId 可选
        
        CommentListVO vo = communityService.getPostComments(postId, page, limit, currentUserId);
        return ResponseEntity.ok(vo);
    }

    /**
     * 【GET /comments/{commentId}/replies】 获取二级评论（回复）列表
     */
    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<List<CommentVO>> getCommentReplies(
        @PathVariable Long commentId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int limit,
        @RequestAttribute(value = "userId", required = false) Long currentUserId) {
        
        List<CommentVO> replies = communityService.getCommentReplies(commentId, page, limit, currentUserId);
        return ResponseEntity.ok(replies);
    }
}