package com.weread.controller.user;

import com.weread.dto.book.RecentBookReviewDTO;
import com.weread.dto.book.UserBookReviewsResponseDTO;
import com.weread.dto.note.BookNoteDTO;
import com.weread.dto.note.UserNotesResponseDTO;
import com.weread.vo.user.FollowListVO;
import com.weread.vo.user.HighlightVO;
import com.weread.vo.user.UserProfileVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import com.weread.dto.user.UpdateProfileDTO;
import com.weread.service.user.UserService;
import com.weread.service.book.BookReviewService;
import com.weread.service.note.NoteService;

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
    private final NoteService noteService;
    
    // ... (构造函数注入) ...
    public UserController(UserService userService, BookReviewService bookReviewService, NoteService noteService) {
        this.userService = userService;
        this.bookReviewService = bookReviewService;
        this.noteService = noteService;
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

    @Operation(summary = "获取登录用户最新的4条书评", description = "获取登录用户最新的4条书评")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/book-reviews/recent")
    public ResponseEntity<Map<String, Object>> getUserRecentReviews(
            @Parameter(description = "用户ID", hidden = true)
            @RequestAttribute(required = false) Integer userId) {
        
        // 检查用户是否已登录
        if (userId == null) {
            // 返回401，无Body
            return ResponseEntity.status(401).build();
        }
        
        // 获取最新的4条书评
        List<RecentBookReviewDTO> reviews = bookReviewService.getUserRecentReviews(userId, 4);
        
        // 构建响应（符合JSON定义格式）
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", reviews);
        
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "获取登录用户的书评瀑布流", 
               description = "瀑布流方式获取用户的所有书评")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/book-reviews")
    public ResponseEntity<Map<String, Object>> getUserBookReviews(
            @Parameter(description = "游标，第一次请求不传，后续传上次返回的nextCursor")
            @RequestParam(required = false) Integer cursor,
            @Parameter(description = "每页数量，默认20，最大50")
            @RequestParam(required = false) Integer limit,
            @Parameter(description = "用户ID", hidden = true)
            @RequestAttribute(required = false) Integer userId) {
        
        // 检查用户是否已登录
        if (userId == null) {
            // 返回401 JSON格式（空对象，符合接口定义）
            Map<String, Object> errorResult = new HashMap<>();
            return ResponseEntity.status(401).body(errorResult);
        }
        
        // 调用Service获取书评列表
        UserBookReviewsResponseDTO response = bookReviewService.getUserReviewsWithCursor(
                userId, cursor, limit);
        
        // 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", response);
        
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "获取登录用户的笔记瀑布流", 
               description = "笔记=划线+想法，这里是获取全部笔记，个人中心会根据noteType进行分流划线和想法，阅读器则调用全部。")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/notes")
    public ResponseEntity<Map<String, Object>> getUserNotes(
            @Parameter(description = "游标，第一次请求不传，后续传上次返回的nextCursor")
            @RequestParam(required = false) Integer cursor,
            @Parameter(description = "每页数量，默认20，最大50")
            @RequestParam(required = false) Integer limit,
            @Parameter(description = "用户ID", hidden = true)
            @RequestAttribute(required = false) Integer userId) {
        
        // 检查用户是否已登录
        if (userId == null) {
            // 返回401 JSON格式（空对象，符合接口定义）
            Map<String, Object> errorResult = new HashMap<>();
            return ResponseEntity.status(401).body(errorResult);
        }
        
        // 将Integer类型的cursor转换为Long类型（因为noteId是Long类型）
        Long cursorLong = cursor != null ? cursor.longValue() : null;
        
        // 调用Service获取笔记列表
        UserNotesResponseDTO response = noteService.getUserNotesWithCursor(
                userId.longValue(), cursorLong, limit);
        
        // 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", response);
        
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "获取登录用户最新的6条笔记", 
               description = "获取登录用户最新的6条笔记")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/notes/recent6")
    public ResponseEntity<Map<String, Object>> getUserRecentNotes6(
            @Parameter(description = "用户ID", hidden = true)
            @RequestAttribute(required = false) Integer userId) {
        
        // 检查用户是否已登录
        if (userId == null) {
            // 返回401，无Body
            return ResponseEntity.status(401).build();
        }
        
        // 获取最新的6条笔记
        List<BookNoteDTO> notes = noteService.getUserRecentNotes6(userId.longValue());
        
        // 构建响应（符合JSON定义格式）
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", notes);
        
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "获取登录用户最新的3条划线", 
               description = "返回用户最新的3条划线记录，包括马克笔、波浪线、下划线三种类型")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/highlights/recent")
    public ResponseEntity<Map<String, Object>> getUserRecentHighlights(
            @Parameter(description = "用户ID", hidden = true)
            @RequestAttribute(required = false) Integer userId) {
        
        // 检查用户是否已登录
        if (userId == null) {
            // 返回401，无Body（根据接口定义）
            return ResponseEntity.status(401).build();
        }
        
        // 获取最新的3条划线
        List<HighlightVO> highlights = noteService.getUserRecentHighlights3(userId.longValue());
        
        // 构建响应（符合JSON定义格式）
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", highlights);
        
        return ResponseEntity.ok(result);
    }
}