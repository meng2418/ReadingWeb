package com.weread.controller;

import com.weread.dto.bookshelf.*;
import com.weread.service.BookshelfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 书架模块 API 接口控制器
 * 对应 OpenAPI 文档中的 [书架] 标签接口
 */
@RestController
@RequestMapping("/api/bookshelf") // 接口基础路径，与文档一致
@RequiredArgsConstructor
@Tag(name = "书架", description = "书架模块相关接口")
@SecurityRequirement(name = "bearerAuth") // 需 JWT 认证，与文档 securitySchemes 一致
public class BookshelfController {

    private final BookshelfService bookshelfService;

    /**
     * 获取书架书籍列表（支持按状态筛选）
     * 对应文档：GET /bookshelf
     */
    @GetMapping
    @Operation(summary = "获取书架", description = "查询用户书架中的书籍，可按阅读状态筛选")
    public ResponseEntity<Map<String, Object>> getBookshelf(
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestHeader("userId") Integer userId) {

        // 构建查询参数DTO（状态筛选）
        BookshelfQueryDTO dto = new BookshelfQueryDTO();
        // 文档中 status 可选值：all/unread/reading/finished，这里转换为 service 层兼容的状态
        if (!"all".equals(status)) {
            dto.setStatus(status);
        }

        // 调用Service查询书架书籍
        List<BookShelfVO> books = bookshelfService.getUserBooks(dto, userId);

        // 按文档响应格式封装：{ "books": [...] }
        return ResponseEntity.ok(Map.of("books", books));
    }

    /**
     * 添加书籍到书架
     * 对应文档：POST /bookshelf
     */
    @PostMapping
    @Operation(summary = "添加到书架", description = "将指定书籍添加到用户书架")
    public ResponseEntity<Map<String, String>> addToBookshelf(
            @RequestBody BookAddDTO dto, // 包含bookId和status
            @RequestHeader("userId") Integer userId) {

        // 调用Service添加书籍
        BookAddVO result = bookshelfService.addBookToShelf(dto, userId);

        // 按文档响应格式返回：{ "message": "添加成功" }
        return new ResponseEntity<>(
                Map.of("message", "书籍已成功添加到书架"),
                HttpStatus.CREATED // 201 Created，与文档一致
        );
    }

    /**
     * 从书架移除书籍
     * 对应文档：DELETE /bookshelf/{bookId}
     */
    @DeleteMapping("/{bookId}")
    @Operation(summary = "从书架移除", description = "将指定书籍从用户书架中删除")
    public ResponseEntity<Map<String, String>> removeFromBookshelf(
            @PathVariable Integer bookId,
            @RequestHeader("userId") Integer userId) {

        // 调用Service移除书籍
        String result = bookshelfService.removeBookFromShelf(bookId, userId);

        // 按文档响应格式返回：{ "message": "移除成功" }
        return ResponseEntity.ok(Map.of("message", result));
    }

    /**
     * 更新书籍阅读状态
     * 对应文档：PUT /bookshelf/{bookId}
     */
    @PutMapping("/{bookId}")
    @Operation(summary = "更新阅读状态", description = "修改书架中书籍的阅读状态（未读/阅读中/已完成）")
    public ResponseEntity<Map<String, String>> updateBookStatus(
            @PathVariable Integer bookId,
            @RequestBody BookStatusUpdateDTO dto, // 包含status和lastReadAt
            @RequestHeader("userId") Integer userId) {

        // 构建状态更新DTO（封装bookId和新状态）
        BookStatusUpdateDTO statusDTO = new BookStatusUpdateDTO();
        statusDTO.setBookId(bookId);
        statusDTO.setStatus(dto.getStatus());

        // 调用Service更新状态
        BookStatusVO result = bookshelfService.updateBookStatus(statusDTO, userId);

        // 按文档响应格式返回：{ "message": "更新成功" }
        return ResponseEntity.ok(Map.of("message", "阅读状态已更新"));
    }

    /**
     * 书架批量操作（删除/更新状态）
     * 对应文档：POST /bookshelf/batch
     */
    @PostMapping("/batch")
    @Operation(summary = "批量操作", description = "批量删除书架书籍或批量更新阅读状态")
    public ResponseEntity<Map<String, String>> batchOperation(
            @RequestBody BookshelfBatchDTO dto, // 包含action、bookIds、status（可选）
            @RequestHeader("userId") Integer userId) {

        // 根据操作类型执行批量处理
        if ("delete".equals(dto.getAction())) {
            // 批量删除
            for (Integer bookId : dto.getBookIds()) {
                bookshelfService.removeBookFromShelf(bookId, userId);
            }
        } else if ("update-status".equals(dto.getAction())) {
            // 批量更新状态
            for (Integer bookId : dto.getBookIds()) {
                BookStatusUpdateDTO statusDTO = new BookStatusUpdateDTO();
                statusDTO.setBookId(bookId);
                statusDTO.setStatus(dto.getStatus());
                bookshelfService.updateBookStatus(statusDTO, userId);
            }
        } else {
            throw new RuntimeException("不支持的操作类型：" + dto.getAction());
        }

        // 按文档响应格式返回：{ "message": "操作成功" }
        return ResponseEntity.ok(Map.of("message", "批量操作成功"));
    }
}