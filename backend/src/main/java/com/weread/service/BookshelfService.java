package com.weread.service;

import com.weread.dto.bookshelf.*;
import java.util.List;

/**
 * 书架模块业务接口
 */
public interface BookshelfService {

    /**
     * 添加书籍到书架
     * 
     * @param dto    包含书籍ID和初始状态
     * @param userId 当前用户ID
     * @return 添加结果VO
     */
    BookAddVO addBookToShelf(BookAddDTO dto, Integer userId);

    /**
     * 从书架移除书籍
     * 
     * @param bookId 书籍ID
     * @param userId 当前用户ID
     * @return 移除结果提示
     */
    String removeBookFromShelf(Integer bookId, Integer userId);

    /**
     * 更新书籍阅读状态
     * 
     * @param dto    包含书籍ID和新状态
     * @param userId 当前用户ID
     * @return 状态更新结果VO
     */
    BookStatusVO updateBookStatus(BookStatusUpdateDTO dto, Integer userId);

    /**
     * 更新阅读进度（章节、页码）
     * 
     * @param dto    包含书籍ID、章节索引、页码
     * @param userId 当前用户ID
     * @return 进度更新结果VO
     */
    ReadingProgressVO updateReadingProgress(ReadingProgressDTO dto, Integer userId);

    /**
     * 分页查询用户书架中的书籍（支持按状态筛选）
     * 
     * @param dto    包含状态筛选条件和分页参数
     * @param userId 当前用户ID
     * @return 书籍列表VO（带分页信息）
     */
    List<BookShelfVO> getUserBooks(BookshelfQueryDTO dto, Integer userId);
}