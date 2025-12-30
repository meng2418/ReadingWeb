package com.weread.service.bookmark;

import com.weread.dto.bookmark.BookmarkCreateDTO;
import com.weread.vo.bookmark.BookmarkVO;

import java.util.List;

/**
 * 书签服务接口
 */
public interface BookmarkService {

    /**
     * 添加书签
     */
    BookmarkVO addBookmark(Long userId, BookmarkCreateDTO dto);

    /**
     * 删除书签
     */
    void deleteBookmark(Long userId, Integer bookmarkId);

    /**
     * 获取用户的所有书签
     */
    List<BookmarkVO> getUserBookmarks(Long userId);

    /**
     * 获取用户某本书的书签
     */
    List<BookmarkVO> getBookBookmarks(Long userId, Integer bookId);
}

