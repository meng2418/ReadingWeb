package com.weread.controller.book;

import com.weread.dto.Result;
import com.weread.entity.user.UserEntity;
import com.weread.service.book.BookService;
import com.weread.service.bookshelf.BookshelfService;
import com.weread.service.book.BookReviewService;
import com.weread.vo.book.AuthorWorkVO;
import com.weread.vo.book.BookDetailVO;
import com.weread.vo.book.BookReviewVO;
import com.weread.vo.book.MarkFinishedVO;
import com.weread.vo.book.RelatedBookVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 书籍详情控制器（兼容前端路径 /books/{bookId}）
 */
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "书籍详情", description = "获取书籍详细信息")
public class BookDetailController {

    private final BookService bookService;
    private final BookReviewService bookReviewService;
    private final BookshelfService bookshelfService;

    @GetMapping("/{bookId}")
    @Operation(summary = "获取书详情", description = "根据ID获取书籍详细信息")
    public Result<BookDetailVO> getBookDetail(
            @PathVariable Integer bookId,
            @AuthenticationPrincipal UserEntity currentUser) {
        Long userId = (currentUser != null && currentUser.getUserId() != null)
                ? currentUser.getUserId().longValue()
                : null;
        return Result.success(bookService.getBookById(bookId, userId));
    }

    @GetMapping("/{bookId}/author-works")
    @Operation(summary = "获取作者代表作3部", description = "根据书籍ID获取该作者的代表作（最多3部，排除当前书籍）")
    public Result<List<AuthorWorkVO>> getAuthorWorks(@PathVariable Integer bookId) {
        List<AuthorWorkVO> works = bookService.getAuthorRepresentativeWorks(bookId);
        return Result.success(works);
    }

    @GetMapping("/{bookId}/related")
    @Operation(summary = "获取相关推荐作品3部", description = "根据书籍ID获取同分类的相关推荐作品（最多3部，排除当前书籍）")
    public Result<List<RelatedBookVO>> getRelatedBooks(@PathVariable Integer bookId) {
        List<RelatedBookVO> relatedBooks = bookService.getRelatedBooks(bookId);
        return Result.success(relatedBooks);
    }

    @GetMapping("/{bookId}/reviews")
    @Operation(summary = "获取用户点评", description = "获取书籍的用户点评列表（最多3条）")
    public Result<List<BookReviewVO>> getBookReviews(@PathVariable Integer bookId) {
        List<BookReviewVO> reviews = bookReviewService.getBookReviewsLimited(bookId, 3);
        return Result.success(reviews);
    }

    @PutMapping("/{bookId}/mark-finished")
    @Operation(summary = "标记书籍为已读完", description = "将指定书籍标记为已读完状态，更新阅读进度为100%，并检查里程碑成就")
    @SecurityRequirement(name = "bearerAuth")
    public Result<MarkFinishedVO> markBookFinished(
            @PathVariable Integer bookId,
            @AuthenticationPrincipal UserEntity currentUser) {
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户未登录");
        }
        Long userId = currentUser.getUserId().longValue();
        MarkFinishedVO result = bookshelfService.markBookFinished(bookId, userId);
        return Result.success(result);
    }
}
