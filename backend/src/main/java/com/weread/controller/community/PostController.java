// 【修改 PostController.java】
package com.weread.controller.community;

// 添加导入
import com.weread.dto.community.PublishPostRequestDTO;
import com.weread.dto.community.RelatedBookDTO;
import com.weread.dto.community.TopicSearchResponseDTO;
import com.weread.service.community.CommentService;
import com.weread.service.community.LikeService;
import com.weread.service.community.PostService;
import com.weread.vo.community.PostVO;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.weread.common.ApiResponse;
import com.weread.dto.community.BookSearchResponseDTO;
import com.weread.dto.community.CommentDTO;
import com.weread.dto.community.CommentResponseDTO;
import com.weread.dto.community.CreateCommentRequestDTO;
import com.weread.dto.community.LikeDetailResponseDTO;
import com.weread.dto.community.LikeResponseDTO;
import com.weread.dto.community.PostCreationDTO;
import com.weread.dto.community.PostDeleteResponseDTO;
import com.weread.dto.community.PostSquareDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/posts")  // 改为 /posts 匹配接口文档
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final LikeService likeService;
    private final CommentService commentService;

    /**
     * GET /posts?type=square|following - 获取广场/关注页帖子
     * 接口文档：type: [square, following]
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostSquareDTO>>> getPosts(
            @RequestParam String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @AuthenticationPrincipal Integer currentUserId) {
    
        try {
            // 参数验证
            if (!"square".equals(type) && !"following".equals(type)) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, "type参数必须是square或following"));
            }
        
            // 转换类型参数
            String mappedType = "square".equals(type) ? "all" : "following";
        
            // 调用服务获取广场帖子
            List<PostSquareDTO> result = postService.getSquarePosts(page, limit, mappedType, currentUserId);
            return ResponseEntity.ok(ApiResponse.ok(result));
        
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            // 返回更详细的错误信息以便调试
            String errorMessage = "获取帖子列表失败: " + e.getMessage();
            if (e.getCause() != null) {
                errorMessage += " - " + e.getCause().getMessage();
            }
            return ResponseEntity.status(500).body(ApiResponse.error(500, errorMessage));
        }
    }

    /**
     * POST /posts - 发布帖子
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPost(
            @AuthenticationPrincipal Integer userId,
            @Valid @RequestBody PublishPostRequestDTO dto) {
    
        try {
            // 将 PublishPostRequestDTO 转换为 PostCreationDTO
            PostCreationDTO creationDTO = new PostCreationDTO();
            creationDTO.setTitle(dto.getTitle());
            creationDTO.setContent(dto.getContent());
            creationDTO.setBookIds(dto.getBookIds());
            creationDTO.setTopics(dto.getTopics());
        
            PostVO newPost = postService.createPost(creationDTO, userId);
        
            // 构建统一响应格式
            Map<String, Object> response = new HashMap<>();
            response.put("code", 201);
            response.put("message", "success");
            response.put("data", newPost);
        
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "发布帖子失败: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * GET /posts/{postId} - 获取帖子详情
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<com.weread.dto.community.PostDetailDTO>> getPostDetail(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer currentUserId) {
        try {
            if (postId == null || postId <= 0) {
                return ResponseEntity.status(400).body(ApiResponse.error(400, "无效的帖子ID"));
            }
            com.weread.dto.community.PostDetailDTO post = postService.getPostById(postId, currentUserId);
            if (post == null) {
                return ResponseEntity.status(404).body(ApiResponse.error(404, "帖子不存在"));
            }
            return ResponseEntity.ok(ApiResponse.ok(post));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getStatusCode().value(), e.getReason()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error(500, "获取帖子详情失败: " + e.getMessage()));
        }
    }

    /**
     * DELETE /posts/{postId} - 删除帖子
     * 接口文档：只能删除自己的帖子
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDeleteResponseDTO>> deletePost(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer userId) {
    
        try {
            boolean deleted = postService.deletePost(postId, userId);
        
            if (!deleted) {
                return ResponseEntity.status(403)
                    .body(ApiResponse.error(403, "无权限删除此帖子或帖子不存在"));
            }
        
            // 获取剩余帖子数
            Integer remainingCount = postService.getUserPostCount(userId);
        
            // 构建响应数据
            PostDeleteResponseDTO responseData = new PostDeleteResponseDTO();
            responseData.setDeletedPostId(postId);
            responseData.setRemainingPostCount(remainingCount);
        
            return ResponseEntity.ok(ApiResponse.ok(responseData));
        
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "删除帖子失败: " + e.getMessage()));
        }
    }

    /**
     * GET /posts/search/books - 发帖时搜索书籍
     */
    @GetMapping("/search/books")
    public ResponseEntity<ApiResponse<BookSearchResponseDTO>> searchBooks(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer cursor,
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
            @RequestParam(required = false) Integer cursor,
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

    /**
     * POST /posts/{postId}/like - 点赞帖子
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<LikeResponseDTO>> likePost(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer userId) {
        
        try {
            LikeResponseDTO result = likeService.togglePostLike(postId, userId);
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getStatusCode().value(), e.getReason()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error(500, "点赞失败"));
        }
    }

    /**
     * DELETE /posts/{postId}/like - 取消点赞帖子
     */
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<LikeResponseDTO>> unlikePost(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer userId) {
        
        try {
            // 这里直接调用 togglePostLike，因为逻辑相同
            LikeResponseDTO result = likeService.togglePostLike(postId, userId);
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getStatusCode().value(), e.getReason()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error(500, "取消点赞失败"));
        }
    }

    /**
     * GET /posts/{postId}/likes/detailed - 获取帖子点赞详情（仅作者可见）
     */
    @GetMapping("/{postId}/likes/detailed")
    public ResponseEntity<ApiResponse<LikeDetailResponseDTO>> getPostLikeDetails(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer userId) {
        
        try {
            LikeDetailResponseDTO result = likeService.getPostLikeDetails(postId, userId);
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getStatusCode().value(), e.getReason()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error(500, "获取点赞详情失败"));
        }
    }

    /**
     * GET /posts/{postId}/comments - 获取评论列表
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getComments(
            @PathVariable Integer postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @AuthenticationPrincipal Integer currentUserId) {
    
        try {
            Page<CommentDTO> result = commentService.getCommentsByPostId(postId, page, limit, currentUserId);
        
            // 从Page中提取内容列表返回，而不是整个Page对象
            List<CommentDTO> comments = result.getContent();
        
            // 或者如果你需要保留分页信息，可以创建自定义响应结构
            return ResponseEntity.ok(ApiResponse.ok(comments));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                .body(ApiResponse.error(e.getStatusCode().value(), e.getReason()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "获取评论列表失败"));
        }
    }

    /**
     * POST /posts/{postId}/comments - 发布帖子评论
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDTO>> createComment(
            @PathVariable Integer postId,
            @Valid @RequestBody CreateCommentRequestDTO request,
            @AuthenticationPrincipal Integer userId) {
        
        try {
            CommentResponseDTO result = commentService.createComment(postId, request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(result));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getStatusCode().value(), e.getReason()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error(500, "发布评论失败"));
        }
    }

    /**
     * GET /posts/{postId}/related-books - 获取帖子相关书籍列表
     */
    @GetMapping("/{postId}/related-books")
    public ResponseEntity<ApiResponse<List<RelatedBookDTO>>> getPostRelatedBooks(
            @PathVariable Integer postId) {
        
        try {
            List<RelatedBookDTO> books = postService.getPostRelatedBooks(postId);
            return ResponseEntity.ok(ApiResponse.ok(books));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getStatusCode().value(), e.getReason()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error(500, "获取相关书籍失败"));
        }
    }
}