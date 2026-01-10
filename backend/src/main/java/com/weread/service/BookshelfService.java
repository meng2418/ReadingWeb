package com.weread.service;

import com.weread.dto.bookshelf.*;
import java.util.List;

/**
 * Bookshelf Module Business Interface.
 */
public interface BookshelfService {

    /**
     * Adds a book to the bookshelf.
     * * @param dto Book ID and initial status
     * @param userId Current User ID
     * @return BookAddVO result
     */
    BookAddVO addBookToShelf(BookAddDTO dto, Integer userId);

    /**
     * Removes a book from the bookshelf.
     * * @param bookId Book ID
     * @param userId Current User ID
     * @return Removal message string
     */
    String removeBookFromShelf(Integer bookId, Integer userId);

    /**
     * Updates the book's reading status.
     * * @param dto Book ID and new status
     * @param userId Current User ID
     * @return Status update result VO
     */
    BookStatusVO updateBookStatus(BookStatusUpdateDTO dto, Integer userId);

    /**
     * Updates the reading progress (chapter, page, progress).
     * * @param dto Book ID, chapter, page, progress details
     * @param userId Current User ID
     * @return Progress update result VO
     */
    ReadingProgressVO updateReadingProgress(ReadingProgressDTO dto, Integer userId);

    /**
     * Queries the user's bookshelf list, supports status filtering.
     * * @param dto Status filter (and pagination if implemented)
     * @param userId Current User ID
     * @return List of BookShelfVO
     */
    List<BookShelfVO> getUserBooks(BookshelfQueryDTO dto, Integer userId);
}