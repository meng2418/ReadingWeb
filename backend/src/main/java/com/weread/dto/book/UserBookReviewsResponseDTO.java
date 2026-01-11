package com.weread.dto.book;

import lombok.Data;
import java.util.List;

/**
 * 用户书评瀑布流响应DTO
 * 对应接口：GET /user/book-reviews
 */
@Data
public class UserBookReviewsResponseDTO {
    private List<BookReviewItemDTO> reviews;  // 书评列表
    private Boolean hasMore;                 // 是否还有更多数据
    private String nextCursor;               // 下一个游标
    private Integer reviewCount;             // 用户总书评数
}

