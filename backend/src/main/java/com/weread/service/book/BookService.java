package com.weread.service.book;

import com.weread.dto.book.BookCreateDTO;
import com.weread.dto.book.BookQueryDTO;
import com.weread.dto.book.BookUpdateDTO;
import com.weread.entity.BookEntity;
import com.weread.vo.book.BookDetailVO;
import com.weread.vo.book.BookListVO;
import com.weread.vo.book.BookSummaryVO;
import org.springframework.data.domain.Page;

/**
 * 书籍服务接口
 */
public interface BookService {
    
    /**
     * 将 BookEntity 转换为用于展示的 BookSummaryVO
     */
    BookSummaryVO convertToSummaryVO(BookEntity bookEntity);

    /**
     * 创建书籍
     */
    BookDetailVO createBook(BookCreateDTO dto);

    /**
     * 更新书籍信息
     */
    BookDetailVO updateBook(Integer bookId, BookUpdateDTO dto);

    /**
     * 根据ID获取书籍详情
     */
    BookDetailVO getBookById(Integer bookId);

    /**
     * 删除书籍（软删除，设置为下架）
     */
    void deleteBook(Integer bookId);

    /**
     * 搜索书籍
     */
    Page<BookListVO> searchBooks(BookQueryDTO queryDTO);

    /**
     * 获取热门书籍
     */
    Page<BookListVO> getPopularBooks(Integer page, Integer size);

    /**
     * 根据分类获取书籍列表
     */
    Page<BookListVO> getBooksByCategory(Integer categoryId, Integer page, Integer size);

    /**
     * 更新书籍状态（上架/下架）
     */
    void updateBookStatus(Integer bookId, Boolean isPublished);

    /**
     * 更新书籍字数统计
     */
    void updateBookWordCount(Integer bookId);
}
