package com.weread.controller.book;

import com.weread.repository.book.UserBookAccessRepository; // 直接使用 Repository 简化查询
import com.weread.vo.book.BookAccessStatusVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/book/access")
@RequiredArgsConstructor
public class BookAccessController {

    private final UserBookAccessRepository userBookAccessRepository;

    /**
     * GET /api/v1/book/access/check/{bookId}
     * 检查用户是否拥有特定书籍的永久阅读权限
     */
    @GetMapping("/check/{bookId}")
    public BookAccessStatusVO checkBookAccess(@RequestAttribute("userId") Long userId, 
                                                @PathVariable Integer bookId) {
        
        boolean hasAccess = userBookAccessRepository.existsByUserIdAndBookId(userId, bookId);

        return BookAccessStatusVO.builder()
                .bookId(bookId)
                .userId(userId)
                .hasPermanentAccess(hasAccess)
                .build();
    }
}