package com.weread.controller.bookshelf;

import com.weread.dto.BookConverter;
import com.weread.dto.Result;
import com.weread.dto.SimpleResult;
import com.weread.dto.book.SimpleBookDTO;
import com.weread.dto.bookshelf.*;
import com.weread.entity.user.UserEntity;
import com.weread.service.bookshelf.BookshelfService;
import com.weread.vo.bookshelf.BookShelfVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * Bookshelf Module Controller.
 * All interface return results are encapsulated with Result<T>.
 */
@RestController
@RequestMapping("/bookshelf")
@RequiredArgsConstructor
@Tag(name = "Bookshelf", description = "Interfaces for bookshelf management")
@SecurityRequirement(name = "bearerAuth") // Require JWT authentication
public class BookshelfController {

    private final BookshelfService bookshelfService;

    /**
     * Retrieve the user's bookshelf list, supports filtering by read status.
     */

    @GetMapping
    @Operation(summary = "获取书架全部书籍")
    public Result<List<SimpleBookDTO>> getBookshelf( // 注意返回类型！
            @RequestParam(required = false, defaultValue = "all") String status) {

        Integer userId = getCurrentUserId();
        BookshelfQueryDTO dto = new BookshelfQueryDTO();

        if (!"all".equals(status)) {
            dto.setStatus(status);
        }

        // 1. 获取原始数据
        List<BookShelfVO> books = bookshelfService.getUserBooks(dto, userId);

        // 2. 转换为与文档一致的DTO
        List<SimpleBookDTO> result = BookConverter.toSimpleBookDTOList(books);

        // 3. 返回转换后的结果
        return Result.success(result);
    }

    @GetMapping("/{status}")
    public Result<List<SimpleBookDTO>> getBookshelfByStatus(@PathVariable String status) {
        Integer userId = getCurrentUserId();
        BookshelfQueryDTO dto = new BookshelfQueryDTO();
        dto.setStatus(status);

        List<BookShelfVO> books = bookshelfService.getUserBooks(dto, userId);
        List<SimpleBookDTO> result = BookConverter.toSimpleBookDTOList(books);

        return Result.success(result);
    }

    @PostMapping("/add/{bookId}")
    @Operation(summary = "添加书籍到书架（简化版）", description = "通过路径参数传入书籍ID，默认状态为未读")
    public Result<Void> addBookToShelfSimple(
            @PathVariable Integer bookId,
            @AuthenticationPrincipal Integer userId) {
    
        BookAddDTO dto = new BookAddDTO();
        dto.setBookId(bookId);
        dto.setStatus("unread");
    
        bookshelfService.addBookToShelf(dto, userId);
        return Result.success();
    }


    /**
     * Remove a book from the bookshelf.
     */
    @DeleteMapping("/{bookId}")
    @Operation(summary = "Remove from Bookshelf", description = "Delete the specified book from the user's bookshelf")
    public Result<Void> removeFromBookshelf(
        @PathVariable Integer bookId,
        @AuthenticationPrincipal Integer userId) {

        
        bookshelfService.removeBookFromShelf(bookId, userId);

        return Result.success();
    }

    /**
     * Update the book's reading status.
     */
    @PutMapping("/{bookId}")
    @Operation(summary = "Update Reading Status", description = "Modify the reading status of a book in the bookshelf (unread/reading/finished)")
    public Result<Void> updateBookStatus(
            @PathVariable Integer bookId,
            @RequestBody BookStatusUpdateDTO dto) {

        Integer userId = getCurrentUserId(); // 从SecurityContext获取用户ID

        BookStatusUpdateDTO statusDTO = new BookStatusUpdateDTO();
        statusDTO.setBookId(bookId);
        statusDTO.setStatus(dto.getStatus());

        bookshelfService.updateBookStatus(statusDTO, userId);

        return Result.success();
    }

    /**
     * Batch operation (delete or update status).
     */
    @DeleteMapping("/batch-remove")
    @Operation(summary = "Batch remove books from bookshelf", description = "批量从书架移除书籍")
    public Result<Void> batchRemove(@RequestBody BatchRemoveDTO dto) {
        // 1. 获取当前用户ID（从SecurityContext）
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return Result.fail("用户ID不能为空");
        }

        // 2. 校验入参（指定400状态码）
        if (dto.getBookIds() == null || dto.getBookIds().isEmpty()) {
            // 明确返回400状态码 + 错误信息
            return Result.fail("bookIds不能为空");
        }

        try {
            // 3. 调用服务层执行删除（捕获异常）
            bookshelfService.batchRemoveBooksFromShelf(dto, userId);
            // 4. 成功响应：带明确提示
            return Result.success();
        } catch (IllegalArgumentException e) {
            // 业务异常（比如书籍不存在）→ 返回400 + 异常信息
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            // 系统异常 → 返回500 + 通用提示
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 从SecurityContext获取当前登录用户ID
     * 需要根据你的UserEntity类调整类型转换
     */
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未认证");
        }   

        Object principal = authentication.getPrincipal();
    
        // 首先处理 Integer 类型（这是你的filter实际设置的）
        if (principal instanceof Integer) {
            return (Integer) principal;
        }
    
        // 然后处理 UserEntity 类型
        if (principal instanceof com.weread.entity.user.UserEntity) {
            UserEntity user = (UserEntity) principal;
            Object userIdObj = user.getUserId();

            if (userIdObj instanceof Integer) {
                return (Integer) userIdObj;
            } else if (userIdObj instanceof Long) {
                return ((Long) userIdObj).intValue(); // Long转Integer
            } else {
                throw new RuntimeException("用户ID类型不支持：" + userIdObj.getClass().getName());
            }
        }
    
        // 处理其他类型
        if (principal instanceof Long) {
            return ((Long) principal).intValue();
        }
    
        if (principal instanceof String) {
            try {
                return Integer.parseInt((String) principal);
            } catch (NumberFormatException e) {
                throw new RuntimeException("用户ID格式错误：" + principal);
            }
        }
    
        throw new RuntimeException("未知的Principal类型：" + principal.getClass().getName());
    }
}