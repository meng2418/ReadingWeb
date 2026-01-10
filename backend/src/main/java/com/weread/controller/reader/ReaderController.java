package com.weread.controller.reader;

import com.weread.dto.Result;
import com.weread.dto.note.ChapterNoteResponseDTO;
import com.weread.entity.user.UserEntity;
import com.weread.service.book.BookChapterService;
import com.weread.service.note.NoteService;
import com.weread.vo.book.ChapterContentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 阅读器控制器
 * 提供阅读器相关的接口，路径匹配JSON接口定义：/books/{bookId}/chapters/{chapterId}
 */
@RestController
@RequestMapping("/books/{bookId}/chapters")
@RequiredArgsConstructor
@Tag(name = "阅读器", description = "阅读器相关接口")
public class ReaderController {

    private final BookChapterService chapterService;
    private final NoteService noteService;

    /**
     * 获取章节信息和内容
     * 接口路径：GET /books/{bookId}/chapters/{chapterId}
     * 匹配JSON接口定义
     */
    @GetMapping("/{chapterId}")
    @Operation(summary = "获取章节信息和内容", description = "获取指定章节的详细信息和内容，用于阅读器显示")
    public Result<ChapterContentVO> getChapterContent(
            @PathVariable String bookId,
            @PathVariable String chapterId,
            @AuthenticationPrincipal UserEntity loginUser) {
        // 将String类型的路径参数转换为Integer
        Integer bookIdInt = parseInteger(bookId, "bookId");
        Integer chapterIdInt = parseInteger(chapterId, "chapterId");
        
        // 获取当前用户ID（如果已登录）
        Integer userId = null;
        if (loginUser != null) {
            userId = loginUser.getUserId();
        }
        return Result.success(chapterService.getChapterContent(bookIdInt, chapterIdInt, userId));
    }

    /**
     * 获取章节笔记列表
     * 接口路径：GET /books/{bookId}/chapters/{chapterId}/notes
     * 匹配JSON接口定义
     */
    @GetMapping("/{chapterId}/notes")
    @Operation(summary = "获取章节笔记列表", description = "获取指定章节的笔记列表，仅返回当前用户的笔记")
    public Result<List<ChapterNoteResponseDTO>> getChapterNotes(
            @PathVariable String bookId,
            @PathVariable String chapterId,
            @AuthenticationPrincipal UserEntity loginUser) {
        // 必须已登录才能获取笔记列表
        if (loginUser == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "未认证");
        }
        
        // 将String类型的路径参数转换为Integer
        Integer bookIdInt = parseInteger(bookId, "bookId");
        Integer chapterIdInt = parseInteger(chapterId, "chapterId");
        
        Long userId = loginUser.getUserId().longValue();
        List<ChapterNoteResponseDTO> notes = noteService.getChapterNotes(userId, bookIdInt, chapterIdInt);
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

