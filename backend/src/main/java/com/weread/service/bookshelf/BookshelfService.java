package com.weread.service.bookshelf;

import com.weread.dto.bookshelf.*;
import com.weread.vo.bookshelf.BookAddVO;
import com.weread.vo.bookshelf.BookShelfVO;
import com.weread.vo.bookshelf.BookStatusVO;
import com.weread.vo.bookshelf.ReadingProgressVO;

import java.util.List;

/**
 * Bookshelf Module Business Interface.
 */
public interface BookshelfService {

    /**
     * Adds a book to the bookshelf.
     * * @param dto Book ID and initial status
     * 
     * @param userId Current User ID
     * @return BookAddVO result
     */
    BookAddVO addBookToShelf(BookAddDTO dto, Long userId);

    /**
     * Removes a book from the bookshelf.
     * * @param bookId Book ID
     * 
     * @param userId Current User ID
     * @return Removal message string
     */
    String removeBookFromShelf(Integer bookId, Long userId);

    /**
     * Updates the book's reading status.
     * * @param dto Book ID and new status
     * 
     * @param userId Current User ID
     * @return Status update result VO
     */
    BookStatusVO updateBookStatus(BookStatusUpdateDTO dto, Long userId);

    /**
     * Updates the reading progress (chapter, page, progress).
     * * @param dto Book ID, chapter, page, progress details
     * 
     * @param userId Current User ID
     * @return Progress update result VO
     */
    ReadingProgressVO updateReadingProgress(ReadingProgressDTO dto, Integer userId);

    /**
     * Queries the user's bookshelf list, supports status filtering.
     * * @param dto Status filter (and pagination if implemented)
     * 
     * @param userId Current User ID
     * @return List of BookShelfVO
     */
    List<BookShelfVO> getUserBooks(BookshelfQueryDTO dto, Long userId);

    /**
     * Marks a book as finished (completed reading).
     * * @param bookId Book ID
     * 
     * @param userId Current User ID
     * @return MarkFinishedVO with book status, finished time, total finished books
     *         count, and milestone info
     */
    com.weread.vo.book.MarkFinishedVO markBookFinished(Integer bookId, Integer userId);

    /**
     * 补充1：查询书架书籍（支持全部/按状态筛选）
     * 适配 /bookshelf 和 /bookshelf/{status} 两个GET接口
     * 
     * @param status   阅读状态（null=全部，unread/reading/finished=按状态查）
     * @param userId   当前用户ID
     * @param isSimple 是否返回简化版（只含cover/title，适配第二个接口）
     * @return 书籍列表（全部字段/简化字段）
     */
    List<?> getUserBooksByStatus(String status, Long userId, boolean isSimple);

    /**
     * 补充2：批量移除书架书籍
     * 适配 /bookshelf/batch-remove DELETE接口
     * 
     * @param dto    批量删除的bookIds
     * @param userId 当前用户ID
     * @return 操作结果提示
     */
    String batchRemoveBooksFromShelf(BatchRemoveDTO dto, Long userId);
}