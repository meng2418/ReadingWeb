package com.weread.controller;

import com.weread.dto.Result;
import com.weread.dto.bookshelf.*;
import com.weread.service.BookshelfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 书架模块控制器（完整改造版）
 * 所有接口响应格式统一为 Result 类型，适配全局异常处理
 */
@RestController
@RequestMapping("/api/bookshelf")
@RequiredArgsConstructor
@Tag(name = "书架", description = "书架模块相关接口")
@SecurityRequirement(name = "bearerAuth") // 需JWT认证
public class BookshelfController {

    private final BookshelfService bookshelfService;

    /**
     * 获取书架书籍列表（支持按状态筛选）
     */
    @GetMapping
    @Operation(summary = "获取书架", description = "查询用户书架中的书籍，可按状态筛选（all/unread/reading/finished）")
    public Result<Map<String, Object>> getBookshelf(
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestHeader("userId") Integer userId) {

        // 构建查询参数DTO
        BookshelfQueryDTO dto = new BookshelfQueryDTO();
        if (!"all".equals(status)) {
            dto.setStatus(status);
        }

        // 调用Service查询
        List<BookShelfVO> books = bookshelfService.getUserBooks(dto, userId);

        // 统一用Result封装响应
        return Result.success(Map.of("books", books));
    }

    /**
     * 添加书籍到书架
     */
    @PostMapping
    @Operation(summary = "添加到书架", description = "将指定书籍添加到用户书架，默认状态为未读")
    public Result<Void> addToBookshelf(
            @RequestBody BookAddDTO dto,
            @RequestHeader("userId") Integer userId) {

        // 调用Service添加
        bookshelfService.addBookToShelf(dto, userId);

        // 无数据返回，仅提示成功
        return Result.success();
    }

    /**
     * 从书架移除书籍
     */
    @DeleteMapping("/{bookId}")
    @Operation(summary = "从书架移除", description = "将指定书籍从用户书架中删除")
    public Result<Void> removeFromBookshelf(
            @PathVariable Integer bookId,
            @RequestHeader("userId") Integer userId) {

        // 调用Service移除
        bookshelfService.removeBookFromShelf(bookId, userId);

        return Result.success();
    }

    /**
     * 更新书籍阅读状态
     */
    @PutMapping("/{bookId}")
    @Operation(summary = "更新阅读状态", description = "修改书架中书籍的阅读状态（unread/reading/finished）")
    public Result<Void> updateBookStatus(
            @PathVariable Integer bookId,
            @RequestBody BookStatusUpdateDTO dto,
            @RequestHeader("userId") Integer userId) {

        // 封装状态更新DTO
        BookStatusUpdateDTO statusDTO = new BookStatusUpdateDTO();
        statusDTO.setBookId(bookId);
        statusDTO.setStatus(dto.getStatus());

        // 调用Service更新
        bookshelfService.updateBookStatus(statusDTO, userId);

        return Result.success();
    }

    /**
     * 书架批量操作（删除/更新状态）
     */
    @PostMapping("/batch")
    @Operation(summary = "批量操作", description = "批量删除书架书籍或批量更新阅读状态")
    public Result<Void> batchOperation(
            @RequestBody BookshelfBatchDTO dto,
            @RequestHeader("userId") Integer userId) {

        // 批量删除
        if ("delete".equals(dto.getAction())) {
            for (Integer bookId : dto.getBookIds()) {
                bookshelfService.removeBookFromShelf(bookId, userId);
            }
        }
        // 批量更新状态
        else if ("update-status".equals(dto.getAction())) {
            for (Integer bookId : dto.getBookIds()) {
                BookStatusUpdateDTO statusDTO = new BookStatusUpdateDTO();
                statusDTO.setBookId(bookId);
                statusDTO.setStatus(dto.getStatus());
                bookshelfService.updateBookStatus(statusDTO, userId);
            }
        }
        // 不支持的操作类型（会被全局异常处理器捕获）
        else {
            throw new RuntimeException("不支持的操作类型：" + dto.getAction());
        }

        return Result.success();
    }
}