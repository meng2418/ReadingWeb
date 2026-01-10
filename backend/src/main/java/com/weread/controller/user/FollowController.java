package com.weread.controller.user;

import com.weread.common.ApiResponse;
import com.weread.dto.response.user.FollowResultResponse;
import com.weread.dto.response.user.FollowingResponse;
import com.weread.vo.user.UserWithFollowVO;
import com.weread.entity.user.UserEntity;
import com.weread.service.user.FollowService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class FollowController {

    private final FollowService followService;

    @Autowired
    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @GetMapping("/following")
    public ResponseEntity<ApiResponse<FollowingResponse>> getFollowing(
        @RequestParam(required = false) Integer cursor,
        @RequestParam(required = false, defaultValue = "20") int limit,
        @AuthenticationPrincipal Integer userId) {

        if (userId == null) {
            // 返回 HTTP 403
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(403, "未登录或 token 无效", null));
        }

        // 正常业务逻辑
        List<UserWithFollowVO> items = followService.getFollowingUsers(userId, cursor, limit);
    
        String nextCursor = items.isEmpty() ? null : items.get(items.size() - 1).getUserId().toString();
        boolean hasMore = items.size() == limit;
    
        FollowingResponse resp = new FollowingResponse();
        resp.setItems(items);
        resp.setHasMore(hasMore);
        resp.setNextCursor(nextCursor);
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Controller Authentication = " + auth);
        // 返回 HTTP 200
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    @GetMapping("/followers")
    public ResponseEntity<ApiResponse<FollowingResponse>> getFollowers(
        @RequestParam(required = false) Integer cursor,
        @RequestParam(required = false, defaultValue = "20") int limit,
        @AuthenticationPrincipal Integer userId) {

        if (userId == null) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(403, "未登录或 token 无效", null));
        }

        List<UserWithFollowVO> items = followService.getFollowers(userId, cursor, limit);

        String nextCursor = items.isEmpty() ? null : items.get(items.size() - 1).getUserId().toString();
        boolean hasMore = items.size() == limit;

        FollowingResponse resp = new FollowingResponse();
        resp.setItems(items);
        resp.setHasMore(hasMore);
        resp.setNextCursor(nextCursor);

        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    @PostMapping("/follow/{userId}")
    public ResponseEntity<ApiResponse<FollowResultResponse>> followUser(
        @PathVariable Integer userId,
        @AuthenticationPrincipal Integer currentUserId) {

        if (currentUserId == null) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(403, "未登录或 token 无效", null));
        }

        FollowResultResponse resp = followService.followUser(currentUserId, userId);
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    @DeleteMapping("/follow/{userId}")
    public ResponseEntity<ApiResponse<FollowResultResponse>> unfollowUser(
        @PathVariable Integer userId,
        @AuthenticationPrincipal Integer currentUserId) {

        if (currentUserId == null) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(403, "未登录或 token 无效", null));
        }

        FollowResultResponse resp = followService.unfollowUser(currentUserId, userId);
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }
}
