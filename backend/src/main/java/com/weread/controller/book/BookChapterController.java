package com.weread.controller.book;

import com.weread.dto.Result;
import com.weread.dto.book.ChapterCreateDTO;
import com.weread.service.book.BookChapterService;
import com.weread.vo.book.ChapterVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 章节管理控制器
 */
@RestController
@RequestMapping("/api/v1/books/{bookId}/chapters")
@RequiredArgsConstructor
@Tag(name = "章节管理", description = "书籍章节的增删改查")
public class BookChapterController {

    private final BookChapterService chapterService;

    @PostMapping
    @Operation(summary = "创建章节", description = "为指定书籍创建新章节")
    public Result<ChapterVO> createChapter(
            @PathVariable Integer bookId,
            @Valid @RequestBody ChapterCreateDTO dto) {
        dto.setBookId(bookId); // 确保bookId一致
        return Result.success(chapterService.createChapter(dto));
    }

    @PutMapping("/{chapterId}")
    @Operation(summary = "更新章节", description = "更新章节信息")
    public Result<ChapterVO> updateChapter(
            @PathVariable Integer bookId,
            @PathVariable Integer chapterId,
            @Valid @RequestBody ChapterCreateDTO dto) {
        dto.setBookId(bookId);
        return Result.success(chapterService.updateChapter(chapterId, dto));
    }

    @GetMapping("/{chapterId}")
    @Operation(summary = "获取章节详情", description = "根据ID获取章节详细信息")
    public Result<ChapterVO> getChapterById(@PathVariable Integer chapterId) {
        return Result.success(chapterService.getChapterById(chapterId));
    }

    @GetMapping
    @Operation(summary = "获取书籍所有章节", description = "获取指定书籍的所有章节列表")
    public Result<List<ChapterVO>> getChaptersByBookId(@PathVariable Integer bookId) {
        return Result.success(chapterService.getChaptersByBookId(bookId));
    }

    @DeleteMapping("/{chapterId}")
    @Operation(summary = "删除章节", description = "删除指定章节")
    public Result<Void> deleteChapter(@PathVariable Integer chapterId) {
        chapterService.deleteChapter(chapterId);
        return Result.success();
    }

    @GetMapping("/{chapterId}/prev")
    @Operation(summary = "获取上一章", description = "获取当前章节的上一章")
    public Result<ChapterVO> getPrevChapter(
            @PathVariable Integer bookId,
            @RequestParam Integer chapterNumber) {
        return Result.success(chapterService.getPrevChapter(bookId, chapterNumber));
    }

    @GetMapping("/{chapterId}/next")
    @Operation(summary = "获取下一章", description = "获取当前章节的下一章")
    public Result<ChapterVO> getNextChapter(
            @PathVariable Integer bookId,
            @RequestParam Integer chapterNumber) {
        return Result.success(chapterService.getNextChapter(bookId, chapterNumber));
    }
}

