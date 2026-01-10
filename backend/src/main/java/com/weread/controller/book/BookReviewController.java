package com.weread.controller.book;

import com.weread.dto.Result;
import com.weread.dto.book.BookReviewCreateDTO;
import com.weread.dto.book.BookReviewCreateResponseDTO;
import com.weread.service.book.BookReviewService;
import com.weread.vo.book.BookReviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 书评控制器
 */
@RestController
@RequestMapping("/book-reviews")
@RequiredArgsConstructor
@Tag(name = "书评管理", description = "书评的创建、查询、删除")
public class BookReviewController {

    private final BookReviewService bookReviewService;

    @PostMapping
    @Operation(summary = "发布书评", description = "用户对书籍进行评价")
    public ResponseEntity<Result<BookReviewCreateResponseDTO>> createReview(
            @RequestAttribute("userId") Integer userId,
            @Valid @RequestBody BookReviewCreateDTO dto) {
        BookReviewVO review = bookReviewService.createReview(userId, dto);
        
        // 使用专门的响应DTO，确保data字段是object类型
        BookReviewCreateResponseDTO response = new BookReviewCreateResponseDTO();
        response.setReview(review);
        
        // HTTP状态码为201（CREATED），但响应体中的code字段应为200（成功）
        Result<BookReviewCreateResponseDTO> result = Result.success(response);
        // 显式确保code字段为200，符合枚举值规范（200、400、401）
        result.setCode(200);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "删除书评", description = "只能删除自己的书评")
    public Result<Map<String, Object>> deleteReview(
            @RequestAttribute("userId") Integer userId,
            @PathVariable Integer reviewId) {
        Integer remainingCount = bookReviewService.deleteReview(userId, reviewId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("deletedReviewId", reviewId);
        response.put("remainingReviewCount", remainingCount);
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

