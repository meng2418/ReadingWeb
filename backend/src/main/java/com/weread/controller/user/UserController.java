package com.weread.controller.user;

import com.weread.dto.Result;
import com.weread.vo.user.FollowListVO;
import com.weread.vo.user.UserProfileVO;
import com.weread.vo.book.BookReviewVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

import com.weread.dto.user.UpdateProfileDTO;
import com.weread.service.user.UserService;
import com.weread.service.book.BookReviewService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {

    private final UserService userService;
    private final BookReviewService bookReviewService;
    
    // ... (构造函数注入) ...
    public UserController(UserService userService, BookReviewService bookReviewService) {
        this.userService = userService;
        this.bookReviewService = bookReviewService;
    }
    /**
     * 【PUT /users/{userId}/follow】 关注用户
     */
    @PutMapping("/{followingId}/follow")
    public ResponseEntity<Void> followUser(
        @PathVariable Integer followingId,
        @RequestAttribute("userId") Integer followerId) { // 当前用户 ID
        
        userService.followUser(followerId, followingId);
        return ResponseEntity.ok().build();
    }

    /**
     * 【DELETE /users/{userId}/follow】 取消关注用户
     */
    @DeleteMapping("/{followingId}/follow")
    public ResponseEntity<Void> unfollowUser(
        @PathVariable Integer followingId,
        @RequestAttribute("userId") Integer followerId) { // 当前用户 ID
        
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
        @RequestAttribute(value = "userId", required = false) Integer currentUserId) {

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
        @RequestAttribute(value = "userId", required = false) Integer currentUserId) {
        
        FollowListVO vo = userService.getFollowings(userId, page, limit, currentUserId);
        return ResponseEntity.ok(vo);
    }

    @Operation(summary = "获取登录用户个人中心")
    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> getUserHome(
            @Parameter(description = "用户ID", hidden = true)
            @RequestAttribute Integer userId) {
        
        UserProfileVO userProfile = userService.getUserHome(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", userProfile);
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "编辑个人信息")
    @PutMapping("/home")
    public ResponseEntity<Map<String, Object>> updateUserProfile(
            @Valid @RequestBody UpdateProfileDTO updateDTO,
            @Parameter(description = "用户ID", hidden = true)
            @RequestAttribute Integer userId) {
        
        UserProfileVO updatedProfile = userService.updateUserProfile(userId, updateDTO);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "个人信息更新成功");
        response.put("data", updatedProfile);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "获取用户最新的3条书评", description = "获取登录用户最新的3条书评")
    @GetMapping("/book-reviews/recent")
    public Result<List<BookReviewVO>> getUserRecentReviews(
            @Parameter(description = "用户ID", hidden = true)
            @RequestAttribute Integer userId) {
        List<BookReviewVO> reviews = bookReviewService.getUserReviewsLimited(userId, 3);
        return Result.success(reviews);
    }
}