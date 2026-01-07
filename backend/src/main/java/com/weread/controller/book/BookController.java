package com.weread.controller.book;

import com.weread.dto.Result;
import com.weread.dto.book.BookCreateDTO;
import com.weread.dto.book.BookQueryDTO;
import com.weread.dto.book.BookUpdateDTO;
import com.weread.service.book.BookReviewService;
import com.weread.service.book.BookService;
import com.weread.vo.book.BookDetailVO;
import com.weread.vo.book.BookListVO;
import com.weread.vo.book.BookReviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 书籍管理控制器
 */
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "书籍管理", description = "书籍信息维护、检索、状态管理")
public class BookController {

    private final BookService bookService;
    private final BookReviewService bookReviewService;

    @PostMapping
    @Operation(summary = "创建书籍", description = "创建新书籍")
    public Result<BookDetailVO> createBook(@Valid @RequestBody BookCreateDTO dto) {
        return Result.success(bookService.createBook(dto));
    }

    @PutMapping("/{bookId}")
    @Operation(summary = "更新书籍", description = "更新书籍信息")
    public Result<BookDetailVO> updateBook(
            @PathVariable Integer bookId,
            @Valid @RequestBody BookUpdateDTO dto) {
        return Result.success(bookService.updateBook(bookId, dto));
    }

    @GetMapping("/{bookId}")
    @Operation(summary = "获取书籍详情", description = "根据ID获取书籍详细信息")
    public Result<BookDetailVO> getBookById(@PathVariable Integer bookId) {
        return Result.success(bookService.getBookById(bookId));
    }

    @DeleteMapping("/{bookId}")
    @Operation(summary = "删除书籍", description = "删除书籍（软删除，设置为下架）")
    public Result<Void> deleteBook(@PathVariable Integer bookId) {
        bookService.deleteBook(bookId);
        return Result.success();
    }

    @GetMapping("/search")
    @Operation(summary = "搜索书籍", description = "根据关键词、分类等条件搜索书籍")
    public Result<Page<BookListVO>> searchBooks(@Valid BookQueryDTO queryDTO) {
        return Result.success(bookService.searchBooks(queryDTO));
    }

    @GetMapping("/popular")
    @Operation(summary = "获取热门书籍", description = "获取热门书籍列表（按阅读量排序）")
    public Result<Page<BookListVO>> getPopularBooks(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(bookService.getPopularBooks(page, size));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "根据分类获取书籍", description = "获取指定分类下的书籍列表")
    public Result<Page<BookListVO>> getBooksByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(bookService.getBooksByCategory(categoryId, page, size));
    }

    @PutMapping("/{bookId}/status")
    @Operation(summary = "更新书籍状态", description = "上架或下架书籍")
    public Result<Void> updateBookStatus(
            @PathVariable Integer bookId,
            @RequestParam Boolean isPublished) {
        bookService.updateBookStatus(bookId, isPublished);
        return Result.success();
    }

    @GetMapping("/{bookId}/reviews")
    @Operation(summary = "获取用户点评", description = "获取书籍的用户点评列表（最多3条）")
    public Result<List<BookReviewVO>> getBookReviews(@PathVariable Integer bookId) {
        List<BookReviewVO> reviews = bookReviewService.getBookReviewsLimited(bookId, 3);
        return Result.success(reviews);
    }
}

