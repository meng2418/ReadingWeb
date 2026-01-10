package com.weread.controller.user;

import com.weread.vo.user.FollowListVO;
import com.weread.vo.user.UserProfileVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

import com.weread.dto.user.UpdateProfileDTO;
import com.weread.service.user.UserService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
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
        @PathVariable Integer followingId,
        @AuthenticationPrincipal Integer followerId) {

        userService.followUser(followerId, followingId);
        return ResponseEntity.ok().build();
    }


    /**
     * 【DELETE /users/{userId}/follow】 取消关注用户
     */
    @DeleteMapping("/{followingId}/follow")
    public ResponseEntity<Void> unfollowUser(
        @PathVariable Integer followingId,
        @AuthenticationPrincipal Integer followerId) {

        userService.unfollowUser(followerId, followingId);
        return ResponseEntity.noContent().build();
    }


    /**
     * 【GET /users/{userId}/followers】 获取粉丝列表
     */
    @GetMapping("/{userId}/followers")
    public ResponseEntity<FollowListVO> getFollowers(
        @PathVariable Integer userId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int limit,
        @AuthenticationPrincipal Integer currentUserId) {

        FollowListVO vo = userService.getFollowers(userId, page, limit, currentUserId);
        return ResponseEntity.ok(vo);
    }

    /**
     * 【GET /users/{userId}/followings】 获取关注列表 (ta 关注了谁)
     */
    @GetMapping("/{userId}/followings")
    public ResponseEntity<FollowListVO> getFollowings(
        @PathVariable Integer userId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int limit,
        @AuthenticationPrincipal Integer currentUserId) {
        
        FollowListVO vo = userService.getFollowings(userId, page, limit, currentUserId);
        return ResponseEntity.ok(vo);
    }

    @Operation(summary = "获取登录用户个人中心")
    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> getUserHome(
            @AuthenticationPrincipal Integer userId) {
        
        UserProfileVO userProfile = userService.getUserHome(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", userProfile);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "编辑个人信息")
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateUserProfile(
            @Valid @RequestBody UpdateProfileDTO updateDTO,
            @AuthenticationPrincipal Integer userId) {
        
        UserProfileVO updatedProfile = userService.updateUserProfile(userId, updateDTO);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "个人信息更新成功");
        response.put("data", updatedProfile);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "获取用户帖子瀑布流")
    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> getUserPosts(
            @Parameter(description = "游标，第一次请求不传")
            @RequestParam(required = false) Integer cursor,
            @Parameter(description = "每页数量，默认10，最大20")
            @RequestParam(defaultValue = "10") Integer limit,
            @AuthenticationPrincipal Integer userId) {
        
        try {
            // 参数验证
            if (limit < 1 || limit > 20) limit = 10;
            
            // 调用服务
            Map<String, Object> result = userService.getUserPosts(userId, cursor, limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "获取用户帖子失败: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}