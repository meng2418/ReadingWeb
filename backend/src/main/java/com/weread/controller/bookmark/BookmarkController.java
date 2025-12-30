package com.weread.controller.bookmark;

import com.weread.dto.Result;
import com.weread.dto.bookmark.BookmarkCreateDTO;
import com.weread.service.bookmark.BookmarkService;
import com.weread.vo.bookmark.BookmarkVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 书签管理控制器
 */
@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
@Tag(name = "书签管理", description = "书签的添加、删除、查询")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping
    @Operation(summary = "添加书签", description = "为当前阅读位置添加书签")
    public Result<BookmarkVO> addBookmark(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody BookmarkCreateDTO dto) {
        return Result.success(bookmarkService.addBookmark(userId, dto));
    }

    @DeleteMapping("/{bookmarkId}")
    @Operation(summary = "删除书签", description = "删除指定书签")
    public Result<Void> deleteBookmark(
            @RequestAttribute("userId") Long userId,
            @PathVariable Integer bookmarkId) {
        bookmarkService.deleteBookmark(userId, bookmarkId);
        return Result.success();
    }

    @GetMapping
    @Operation(summary = "获取所有书签", description = "获取当前用户的所有书签")
    public Result<List<BookmarkVO>> getUserBookmarks(@RequestAttribute("userId") Long userId) {
        return Result.success(bookmarkService.getUserBookmarks(userId));
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "获取书籍书签", description = "获取指定书籍的所有书签")
    public Result<List<BookmarkVO>> getBookBookmarks(
            @RequestAttribute("userId") Long userId,
            @PathVariable Integer bookId) {
        return Result.success(bookmarkService.getBookBookmarks(userId, bookId));
    }
}

