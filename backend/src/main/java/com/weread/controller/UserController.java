package com.weread.controller;

import com.weread.vo.user.FollowListVO;
import com.weread.service.user.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    // ... (构造函数注入) ...
    public UserController(UserService userService) {
        this.userService = userService;
    }
    /**
     * 【PUT /users/{userId}/follow】 关注用户
     */
    @PutMapping("/{followingId}/follow")
    public ResponseEntity<Void> followUser(
        @PathVariable Long followingId,
        @RequestAttribute("userId") Long followerId) { // 当前用户 ID
        
        userService.followUser(followerId, followingId);
        return ResponseEntity.ok().build();
    }

    /**
     * 【DELETE /users/{userId}/follow】 取消关注用户
     */
    @DeleteMapping("/{followingId}/follow")
    public ResponseEntity<Void> unfollowUser(
        @PathVariable Long followingId,
        @RequestAttribute("userId") Long followerId) { // 当前用户 ID
        
        userService.unfollowUser(followerId, followingId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 【GET /users/{userId}/followers】 获取粉丝列表
     */
    @GetMapping("/{userId}/followers")
    public ResponseEntity<FollowListVO> getFollowers(
        @PathVariable Long userId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int limit,
        @RequestAttribute(value = "userId", required = false) Long currentUserId) {

        FollowListVO vo = userService.getFollowers(userId, page, limit, currentUserId);
        return ResponseEntity.ok(vo);
    }

    /**
     * 【GET /users/{userId}/followings】 获取关注列表 (ta 关注了谁)
     */
    @GetMapping("/{userId}/followings")
    public ResponseEntity<FollowListVO> getFollowings(
        @PathVariable Long userId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int limit,
        @RequestAttribute(value = "userId", required = false) Long currentUserId) {
        
        FollowListVO vo = userService.getFollowings(userId, page, limit, currentUserId);
        return ResponseEntity.ok(vo);
    }
}