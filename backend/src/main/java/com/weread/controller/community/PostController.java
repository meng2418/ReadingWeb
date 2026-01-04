// 【修改 PostController.java】
package com.weread.controller.community;

// 添加导入
import com.weread.dto.community.PublishPostRequestDTO;
import com.weread.dto.community.TopicSearchResponseDTO;
import com.weread.service.community.PostService;
import com.weread.vo.community.PostListVO;
import com.weread.vo.community.PostVO;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.weread.common.ApiResponse;
import com.weread.dto.community.BookSearchResponseDTO;
import com.weread.dto.community.PostCreationDTO;
import com.weread.dto.community.PostDeleteResponseDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")  // 改为 /posts 匹配接口文档
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * GET /posts?type=square|following - 获取广场/关注页帖子
     * 接口文档：type: [square, following]
     */
    @GetMapping
    public ResponseEntity<?> getPosts(
            @RequestParam String type,  // square, following
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestAttribute(value = "userId", required = false) Integer currentUserId) {
        
        // 将接口文档的 square/following 映射到业务逻辑
        String mappedType;
        if ("square".equals(type)) {
            mappedType = "all";  // 广场对应全部帖子
        } else if ("following".equals(type)) {
            mappedType = "following";  // 关注页
        } else {
            return ResponseEntity.badRequest().body("type参数必须是square或following");
        }
        
        PostListVO result = postService.getPostList(page, limit, mappedType, null, currentUserId);
        return ResponseEntity.ok(result);
    }

    /**
     * POST /posts - 发布帖子
     * 接口文档：使用 PublishPostRequest
     */
    @PostMapping
    public ResponseEntity<PostVO> createPost(
            @RequestAttribute(value = "userId", required = true) Integer userId,
            @Valid @RequestBody PublishPostRequestDTO dto) {
        
        // 将 PublishPostRequestDTO 转换为 PostCreationDTO
        PostCreationDTO creationDTO = new PostCreationDTO();
        creationDTO.setTitle(dto.getTitle());
        creationDTO.setContent(dto.getContent());
        creationDTO.setBookIds(dto.getBookIds());
        creationDTO.setTopics(dto.getTopics());
        creationDTO.setPublishLocation(dto.getPublishLocation());
        
        PostVO newPost = postService.createPost(creationDTO, userId);
        return new ResponseEntity<>(newPost, HttpStatus.CREATED);
    }

    /**
     * GET /posts/{postId} - 获取帖子详情
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
     * DELETE /posts/{postId} - 删除帖子
     * 接口文档：只能删除自己的帖子
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostDeleteResponseDTO> deletePost(
            @PathVariable Integer postId,
            @RequestAttribute(value = "userId", required = true) Integer userId) {
        
        // 这里需要调用删除服务
        // 由于PostService没有删除方法，需要添加
        boolean deleted = postService.deletePost(postId, userId);
        
        if (!deleted) {
            return ResponseEntity.status(403).body(
                new PostDeleteResponseDTO(false, "无权限删除此帖子或帖子不存在", postId, null, null)
            );
        }
        
        // 获取剩余帖子数（需要实现）
        Integer remainingCount = postService.getUserPostCount(userId);
        
        return ResponseEntity.ok(
            new PostDeleteResponseDTO(true, "删除成功", postId, remainingCount, null)
        );
    }

    /**
     * GET /posts/search/books - 发帖时搜索书籍
     */
    @GetMapping("/search/books")
    public ResponseEntity<ApiResponse<BookSearchResponseDTO>> searchBooks(
            @RequestParam String keyword,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int limit) {
        
        try {
            BookSearchResponseDTO result = postService.searchBooks(keyword, cursor, limit);
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error(500, "搜索书籍失败"));
        }
    }

    /**
     * GET /posts/search/topics - 发帖时搜索话题
     */
    @GetMapping("/search/topics")
    public ResponseEntity<ApiResponse<TopicSearchResponseDTO>> searchTopics(
            @RequestParam String keyword,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int limit) {
        
        try {
            TopicSearchResponseDTO result = postService.searchTopics(keyword, cursor, limit);
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error(500, "搜索话题失败"));
        }
    }
}