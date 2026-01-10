package com.weread.controller.author;

import com.weread.common.ApiResponse;
import com.weread.service.author.AuthorService;
import com.weread.vo.author.AuthorDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 作者控制器
 */
@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {
    
    private final AuthorService authorService;
    
    /**
     * 获取作者详情
     * GET /authors/{authorId}
     */
    @GetMapping("/{authorId}")
    public ResponseEntity<ApiResponse<AuthorDetailVO>> getAuthorDetail(
            @PathVariable Integer authorId,
            @RequestAttribute(value = "userId", required = false) Integer currentUserId) {
        
        try {
            AuthorDetailVO authorDetail = authorService.getAuthorDetail(authorId, currentUserId);
            return ResponseEntity.ok(ApiResponse.ok(authorDetail));
        } catch (RuntimeException e) {
            // 作者不存在，返回404
            if (e.getMessage().contains("不存在")) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error(404, e.getMessage()));
            }
            // 其他错误返回500
            return ResponseEntity.status(500)
                    .body(ApiResponse.error(500, "获取作者详情失败: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(ApiResponse.error(500, "获取作者详情失败"));
        }
    }
}

