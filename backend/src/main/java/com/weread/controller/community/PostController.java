package com.weread.controller.community;

import com.weread.dto.community.PostCreationDTO;
import com.weread.service.community.PostService;
import com.weread.vo.community.PostListVO;
import com.weread.vo.community.PostVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 假设您有一个安全配置，能够将当前登录用户的 ID 注入到请求属性中
    // 如果用户未登录，@RequestAttribute 应该允许为 null

    /**
     * POST /api/v1/posts
     * 【发帖】用户创建新帖子
     * * @param userId 当前登录用户的ID
     * @param dto 帖子内容DTO (包含标题、内容、话题/标签列表)
     * @return PostVO 新创建的帖子详情
     */
    @PostMapping
    public ResponseEntity<PostVO> createPost(
            @RequestAttribute(value = "userId", required = true) Integer userId, 
            @Valid @RequestBody PostCreationDTO dto) {
        
        // userId 必须存在才能发帖，如果 required=true，则未登录用户将收到 401/403 错误（由安全框架处理）
        PostVO newPost = postService.createPost(dto, userId);
        return new ResponseEntity<>(newPost, HttpStatus.CREATED);
    }

    /**
     * GET /api/v1/posts/{postId}
     * 【获取帖子详情】
     * * @param postId 帖子ID
     * @return PostVO 帖子详情
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostVO> getPostDetail(@PathVariable Integer postId) {
        
        PostVO post = postService.getPostById(postId);
        
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(post);
    }

    /**
     * GET /api/v1/posts
     * 【获取帖子列表】
     * 用于发现页、热门列表、最新列表等。
     * * @param page 页码
     * @param limit 每页数量
     * @param type 列表类型 (例如: "HOT", "LATEST", "FOLLOWING")
     * @param hashtags 话题/标签列表，用于筛选
     * @param currentUserId 当前登录用户的ID（如果未登录，为 null）
     * @return PostListVO 帖子列表分页数据
     */
    @GetMapping
    public PostListVO getPostList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "LATEST") String type,
            @RequestParam(required = false) List<String> hashtags,
            @RequestAttribute(value = "userId", required = false) Integer currentUserId) {
        
        return postService.getPostList(page, limit, type, hashtags, currentUserId);
    }
}