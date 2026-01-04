// CommentController.java
package com.weread.controller.community;

import com.weread.common.ApiResponse;
import com.weread.dto.community.*;
import com.weread.service.community.LikeService;
import com.weread.service.community.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final LikeService likeService;
    private final CommentService commentService;

    /**
     * POST /comments/{commentId}/like - 点赞评论
     */
    @PostMapping("/{commentId}/like")
    public ResponseEntity<ApiResponse<LikeResponseDTO>> likeComment(
            @PathVariable Integer commentId,
            @RequestAttribute(value = "userId", required = true) Integer userId) {
        
        try {
            LikeResponseDTO result = likeService.toggleCommentLike(commentId, userId);
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getStatusCode().value(), e.getReason()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error(500, "点赞评论失败"));
        }
    }

    /**
     * DELETE /comments/{commentId}/like - 取消点赞评论
     */
    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<ApiResponse<LikeResponseDTO>> unlikeComment(
            @PathVariable Integer commentId,
            @RequestAttribute(value = "userId", required = true) Integer userId) {
        
        try {
            LikeResponseDTO result = likeService.toggleCommentLike(commentId, userId);
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getStatusCode().value(), e.getReason()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error(500, "取消点赞评论失败"));
        }
    }

    /**
     * POST /comments/{commentId}/reply - 回复评论
     */
    @PostMapping("/{commentId}/reply")
    public ResponseEntity<ApiResponse<ReplyResponseDTO>> replyComment(
            @PathVariable Integer commentId,
            @Valid @RequestBody CreateCommentRequestDTO request,
            @RequestAttribute(value = "userId", required = true) Integer userId) {
        
        try {
            ReplyResponseDTO result = commentService.replyComment(commentId, request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(result));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getStatusCode().value(), e.getReason()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error(500, "回复评论失败"));
        }
    }

    /**
     * GET /comments/{commentId} - 获取评论详情
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentDTO>> getCommentDetail(
            @PathVariable Integer commentId,
            @RequestAttribute(value = "userId", required = false) Integer currentUserId) {
        
        try {
            CommentDTO result = commentService.getCommentById(commentId, currentUserId);
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getStatusCode().value(), e.getReason()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error(500, "获取评论详情失败"));
        }
    }
}