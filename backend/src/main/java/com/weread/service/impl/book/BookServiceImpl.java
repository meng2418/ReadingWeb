package com.weread.service.impl.book;

import com.weread.entity.BookEntity;
import com.weread.vo.book.BookSummaryVO;
import com.weread.service.book.BookService;

import org.springframework.stereotype.Service;
// ... (其他导入)

@Service
public class BookServiceImpl implements BookService {
    
    // ... (注入 AuthorRepository, BookRepository 等) ...

    @Override // 【✅ 必须有 @Override 确保正确实现接口】
    public BookSummaryVO convertToSummaryVO(BookEntity bookEntity) {
        BookSummaryVO vo = new BookSummaryVO();
        
        // 1. 设置 BookEntity 中的直接字段
        vo.setBookId(bookEntity.getBookId());
        vo.setTitle(bookEntity.getTitle());
        vo.setCover(bookEntity.getCover());
        vo.setDescription(bookEntity.getDescription());
        
        // 2. 组装关联的作者名称 (需要额外的查询或关联实体)
        // 假设 bookEntity.getAuthor() 能够获取到关联的 AuthorEntity
        if (bookEntity.getAuthor() != null) {
            vo.setAuthorName(bookEntity.getAuthor().getName());
        }
        
        return vo;
    }
    
    // ... (其他方法实现)
}