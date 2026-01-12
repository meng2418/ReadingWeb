package com.weread.controller.reader;

import com.weread.dto.Result;
import com.weread.dto.book.ChapterDTO;
import com.weread.dto.note.BookNoteResponseDTO;
import com.weread.entity.user.UserEntity;
import com.weread.service.book.BookChapterService;
import com.weread.service.note.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 阅读器目录控制器
 * 提供阅读器相关的目录接口，路径匹配JSON接口定义：/reader/{bookId}/chapters
 */
@RestController
@RequestMapping("/reader")
@RequiredArgsConstructor
@Tag(name = "阅读器", description = "阅读器相关接口")
@SecurityRequirement(name = "bearerAuth")
public class ReaderTOCController {

    private final BookChapterService chapterService;
    private final NoteService noteService;

    /**
     * 获取书籍目录
     * 接口路径：GET /reader/{bookId}/chapters
     * 匹配JSON接口定义
     */
    @GetMapping("/{bookId}/chapters")
    @Operation(summary = "获取书籍目录", description = "获取指定书籍的目录列表，包含章节的起始页码、章节序号和章节名称")
    public Result<List<ChapterDTO>> getBookChapters(
            @PathVariable String bookId) {
        // 将String类型的路径参数转换为Integer
        Integer bookIdInt = parseInteger(bookId, "bookId");
        
        // 获取书籍目录
        List<ChapterDTO> chapters = chapterService.getBookChapters(bookIdInt);
        return Result.success(chapters);
    }

    /**
     * 获取全书笔记列表
     * 接口路径：GET /reader/{bookId}/notes
     * 匹配JSON接口定义
     */
    @GetMapping("/{bookId}/notes")
    @Operation(summary = "获取全书笔记列表", description = "获取指定书籍的所有笔记列表，仅返回当前用户的笔记")
    public Result<List<BookNoteResponseDTO>> getBookNotes(
            @PathVariable String bookId,
            @AuthenticationPrincipal Integer userId) {
        // 必须已登录才能获取笔记列表
        if (userId == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "未认证");
        }
        
        // 将String类型的路径参数转换为Integer
        Integer bookIdInt = parseInteger(bookId, "bookId");
        
        List<BookNoteResponseDTO> notes = noteService.getBookNotes(userId, bookIdInt);
        return Result.success(notes);
    }

    /**
     * 将String类型的路径参数转换为Integer
     * @param value 字符串值
     * @param paramName 参数名称（用于错误提示）
     * @return Integer值
     * @throws ResponseStatusException 如果转换失败
     */
    private Integer parseInteger(String value, String paramName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, 
                    paramName + "必须是有效的整数");
        }
    }
}

