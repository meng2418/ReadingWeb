package com.weread.controller.community;

import com.weread.common.ApiResponse;
import com.weread.service.community.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * GET /messages/my-posts/comments - 获取我的帖子的评论瀑布流
     */
    @GetMapping("/my-posts/comments")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyPostsComments(
            @RequestParam(required = false) Integer cursor,
            @RequestParam(defaultValue = "20") Integer limit,
            @AuthenticationPrincipal Integer userId) {
        
        try {
            // 参数验证
            if (limit < 1 || limit > 50) limit = 20;
            
            // 调用服务
            Map<String, Object> result = messageService.getMyPostsComments(userId, cursor, limit);
            
            return ResponseEntity.ok(ApiResponse.ok(result));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(ApiResponse.error(500, "获取评论列表失败: " + e.getMessage()));
        }
    }

    /**
     * GET /messages/likes - 获取我的点赞瀑布流
     */
    @GetMapping("/likes")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyLikes(
            @RequestParam(required = false) Integer cursor,
            @RequestParam(defaultValue = "20") Integer limit,
            @AuthenticationPrincipal Integer userId) {
        
        try {
            // 参数验证
            if (limit < 1 || limit > 50) limit = 20;
            
            // 调用服务
            Map<String, Object> result = messageService.getMyLikes(userId, cursor, limit);
            
            return ResponseEntity.ok(ApiResponse.ok(result));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(ApiResponse.error(500, "获取点赞列表失败: " + e.getMessage()));
        }
    }
}