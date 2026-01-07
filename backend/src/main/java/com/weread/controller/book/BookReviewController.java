package com.weread.controller.book;

import com.weread.dto.Result;
import com.weread.dto.book.BookReviewCreateDTO;
import com.weread.service.book.BookReviewService;
import com.weread.vo.book.BookReviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 书评控制器
 */
@RestController
@RequestMapping("/api/v1/book-reviews")
@RequiredArgsConstructor
@Tag(name = "书评管理", description = "书评的创建、查询、删除")
public class BookReviewController {

    private final BookReviewService bookReviewService;

    @PostMapping
    @Operation(summary = "发布书评", description = "用户对书籍进行评价")
    public Result<Map<String, Object>> createReview(
            @RequestAttribute("userId") Integer userId,
            @Valid @RequestBody BookReviewCreateDTO dto) {
        BookReviewVO review = bookReviewService.createReview(userId, dto);
        
        Map<String, Object> response = new HashMap<>();
        response.put("review", review);
        return Result.success(response);
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "删除书评", description = "只能删除自己的书评")
    public Result<Map<String, Object>> deleteReview(
            @RequestAttribute("userId") Integer userId,
            @PathVariable Integer reviewId) {
        bookReviewService.deleteReview(userId, reviewId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("deletedReviewId", reviewId);
        // 可以返回剩余评论数，这里简化处理
        response.put("remainingReviewCount", 0);
        return Result.success(response);
    }

    @GetMapping
    @Operation(summary = "获取书评列表", description = "分页获取书评列表")
    public Result<Page<BookReviewVO>> getReviews(
            @RequestParam(required = false) Integer bookId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        if (bookId == null) {
            return Result.fail("书籍ID不能为空");
        }
        Page<BookReviewVO> reviews = bookReviewService.getBookReviews(bookId, page, size);
        return Result.success(reviews);
    }
}

