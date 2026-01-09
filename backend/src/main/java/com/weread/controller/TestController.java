package com.weread.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class TestController {

    // 0. 测试根路径 - 用于验证Controller是否生效
    @GetMapping("/test")
    public Map<String, Object> test() {
        log.info("=== 测试接口被调用 ===");
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "TestController工作正常");
        response.put("timestamp", new Date());
        return response;
    }

    // 注意：获取作者详情接口已迁移到 AuthorController
    // GET /authors/{authorId} 现在由 AuthorController 处理

    // 2. GET /books/{bookId} - 获取书详情（调用真正的服务）
    // 注意：这个接口现在调用 BookService 来获取真实的书籍详情
    // 如果前端需要，可以保持这个路径，或者迁移到 BookController
    // 为了保持兼容性，暂时保留此接口，但建议前端使用 /api/v1/books/{bookId}

    // 2.1 GET /books/ - 处理不带参数的请求
    @GetMapping("/books/")
    public Map<String, Object> getBooksList() {
        log.info("=== 收到请求 GET /books/ ===");

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "成功");
        response.put("data", "这是书籍列表接口，请提供bookId获取具体书籍信息");

        return response;
    }

    // 3. POST /book-reviews - 发布书评（已迁移到 BookReviewController）
    // 注意：此接口已废弃，请使用 /api/v1/book-reviews
    // @PostMapping("/book-reviews")
    // public ResponseEntity<Map<String, Object>> publishReview(@RequestBody Map<String, Object> request) {
    //     // 已迁移到 BookReviewController
    // }

    // 4. PUT /books/{bookId}/mark-finished - 标记书籍为已读完
    @PutMapping("/books/{bookId}/mark-finished")
    public Map<String, Object> markBookFinished(@PathVariable Integer bookId) {
        log.info("=== 收到请求 PUT /books/{}/mark-finished ===", bookId);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "书籍标记为已读完");
        response.put("data", Map.of(
                "bookId", bookId,
                "status", "finished",
                "updatedAt", new Date()
        ));

        return response;
    }
}