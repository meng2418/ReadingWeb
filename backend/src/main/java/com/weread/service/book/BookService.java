package com.weread.service.book;

import com.weread.entity.BookEntity;
import com.weread.vo.book.BookSummaryVO;
// ... (其他导入)

public interface BookService {
    
    // 【✅ 必须添加此方法签名】
    /**
     * 将 BookEntity 转换为用于展示的 BookSummaryVO，并组装关联的作者名称等信息。
     * @param bookEntity 书籍实体
     * @return 简要书籍展示对象
     */
    BookSummaryVO convertToSummaryVO(BookEntity bookEntity);
    
    // ... (其他方法，如 getBookById, createBook)
}