package com.weread.service.impl.bookmark;

import com.weread.dto.bookmark.BookmarkCreateDTO;
import com.weread.entity.BookmarkEntity;
import com.weread.repository.BookRepository;
import com.weread.repository.BookmarkRepository;
import com.weread.repository.book.BookChapterRepository;
import com.weread.service.bookmark.BookmarkService;
import com.weread.vo.bookmark.BookmarkVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 书签服务实现类
 */
@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final BookRepository bookRepository;
    private final BookChapterRepository chapterRepository;

    @Override
    @Transactional
    public BookmarkVO addBookmark(Long userId, BookmarkCreateDTO dto) {
        // 检查是否已存在
        bookmarkRepository.findByUserIdAndBookIdAndChapterId(
                userId, dto.getBookId(), dto.getChapterId())
                .ifPresent(b -> {
                    throw new RuntimeException("该书签已存在");
                });

        // 创建书签
        BookmarkEntity bookmark = new BookmarkEntity();
        bookmark.setUserId(userId);
        bookmark.setBookId(dto.getBookId());
        bookmark.setChapterId(dto.getChapterId());
        bookmark.setPageNumber(dto.getPageNumber());
        bookmark.setNote(dto.getNote());

        bookmark = bookmarkRepository.save(bookmark);
        return convertToVO(bookmark);
    }

    @Override
    @Transactional
    public void deleteBookmark(Long userId, Integer bookmarkId) {
        BookmarkEntity bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new RuntimeException("书签不存在"));

        if (!bookmark.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该书签");
        }

        bookmarkRepository.delete(bookmark);
    }

    @Override
    public List<BookmarkVO> getUserBookmarks(Long userId) {
        List<BookmarkEntity> bookmarks = bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return bookmarks.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookmarkVO> getBookBookmarks(Long userId, Integer bookId) {
        List<BookmarkEntity> bookmarks = bookmarkRepository.findByUserIdAndBookIdOrderByCreatedAtDesc(userId, bookId);
        return bookmarks.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private BookmarkVO convertToVO(BookmarkEntity bookmark) {
        BookmarkVO vo = new BookmarkVO();
        vo.setBookmarkId(bookmark.getBookmarkId());
        vo.setBookId(bookmark.getBookId());
        vo.setChapterId(bookmark.getChapterId());
        vo.setPageNumber(bookmark.getPageNumber());
        vo.setNote(bookmark.getNote());
        vo.setCreatedAt(bookmark.getCreatedAt());

        // 获取书籍和章节信息
        bookRepository.findByBookId(bookmark.getBookId())
                .ifPresent(book -> vo.setBookTitle(book.getTitle()));

        chapterRepository.findById(bookmark.getChapterId())
                .ifPresent(chapter -> vo.setChapterTitle(chapter.getTitle()));

        return vo;
    }
}

