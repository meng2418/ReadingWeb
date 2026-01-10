package com.weread.dto.book;

import com.weread.vo.book.BookReviewVO;
import lombok.Data;

/**
 * 发布书评响应DTO
 * 确保data字段是object类型
 */
@Data
public class BookReviewCreateResponseDTO {
    /**
     * 书评信息
     */
    private BookReviewVO review;
}



