package com.weread.service.impl.book;

import com.weread.dto.book.ChapterCreateDTO;
import com.weread.entity.BookEntity;
import com.weread.entity.book.BookChapterEntity;
import com.weread.repository.BookRepository;
import com.weread.repository.book.BookChapterRepository;
import com.weread.service.book.BookChapterService;
import com.weread.service.book.BookService;
import com.weread.vo.book.ChapterVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 章节服务实现类
 */
@Service
@RequiredArgsConstructor
public class BookChapterServiceImpl implements BookChapterService {

    private final BookChapterRepository chapterRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;

    @Override
    @Transactional
    public ChapterVO createChapter(ChapterCreateDTO dto) {
        // 验证书籍是否存在
        BookEntity book = bookRepository.findByBookId(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("书籍不存在"));

        // 检查章节序号是否已存在
        chapterRepository.findByBookIdAndChapterNumber(dto.getBookId(), dto.getChapterNumber())
                .ifPresent(c -> {
                    throw new RuntimeException("该章节序号已存在");
                });

        // 创建章节
        BookChapterEntity chapter = new BookChapterEntity();
        chapter.setBookId(dto.getBookId());
        chapter.setTitle(dto.getTitle());
        chapter.setChapterNumber(dto.getChapterNumber());
        chapter.setContent(dto.getContent());
        chapter.setIsVip(dto.getIsVip() != null ? dto.getIsVip() : false);
        chapter.setIsPublished(dto.getIsPublished() != null ? dto.getIsPublished() : true);

        // 计算字数
        Integer wordCount = calculateWordCount(dto.getContent());
        chapter.setWordCount(wordCount);

        // 设置上一章和下一章
        setChapterLinks(chapter);

        chapter = chapterRepository.save(chapter);

        // 更新书籍总字数
        bookService.updateBookWordCount(dto.getBookId());

        return convertToVO(chapter);
    }

    @Override
    @Transactional
    public ChapterVO updateChapter(Integer chapterId, ChapterCreateDTO dto) {
        BookChapterEntity chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("章节不存在"));

        // 更新字段
        if (dto.getTitle() != null) {
            chapter.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null) {
            chapter.setContent(dto.getContent());
            // 重新计算字数
            chapter.setWordCount(calculateWordCount(dto.getContent()));
        }
        if (dto.getChapterNumber() != null) {
            chapter.setChapterNumber(dto.getChapterNumber());
        }
        if (dto.getIsVip() != null) {
            chapter.setIsVip(dto.getIsVip());
        }
        if (dto.getIsPublished() != null) {
            chapter.setIsPublished(dto.getIsPublished());
        }

        // 重新设置章节链接
        setChapterLinks(chapter);

        chapter = chapterRepository.save(chapter);

        // 更新书籍总字数
        bookService.updateBookWordCount(chapter.getBookId());

        return convertToVO(chapter);
    }

    @Override
    public ChapterVO getChapterById(Integer chapterId) {
        BookChapterEntity chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("章节不存在"));
        return convertToVO(chapter);
    }

    @Override
    public List<ChapterVO> getChaptersByBookId(Integer bookId) {
        List<BookChapterEntity> chapters = chapterRepository.findByBookIdOrderByChapterNumberAsc(bookId);
        return chapters.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteChapter(Integer chapterId) {
        BookChapterEntity chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("章节不存在"));
        
        Integer bookId = chapter.getBookId();
        chapterRepository.delete(chapter);

        // 更新书籍总字数
        bookService.updateBookWordCount(bookId);
    }

    @Override
    public ChapterVO getPrevChapter(Integer bookId, Integer chapterNumber) {
        return chapterRepository
                .findByBookIdAndChapterNumberLessThanOrderByChapterNumberDesc(bookId, chapterNumber)
                .map(this::convertToVO)
                .orElse(null);
    }

    @Override
    public ChapterVO getNextChapter(Integer bookId, Integer chapterNumber) {
        return chapterRepository
                .findByBookIdAndChapterNumberGreaterThanOrderByChapterNumberAsc(bookId, chapterNumber)
                .map(this::convertToVO)
                .orElse(null);
    }

    @Override
    public Integer calculateWordCount(String content) {
        if (content == null || content.trim().isEmpty()) {
            return 0;
        }
        // 简单的中文字数统计：去除空格和标点，统计字符数
        String text = content.replaceAll("\\s+", "").replaceAll("[\\p{Punct}]", "");
        return text.length();
    }

    /**
     * 设置章节的上一章和下一章链接
     */
    private void setChapterLinks(BookChapterEntity chapter) {
        Integer bookId = chapter.getBookId();
        Integer chapterNumber = chapter.getChapterNumber();

        // 查找上一章
        chapterRepository
                .findByBookIdAndChapterNumberLessThanOrderByChapterNumberDesc(bookId, chapterNumber)
                .ifPresent(prev -> chapter.setPrevChapterId(prev.getChapterId()));

        // 查找下一章
        chapterRepository
                .findByBookIdAndChapterNumberGreaterThanOrderByChapterNumberAsc(bookId, chapterNumber)
                .ifPresent(next -> chapter.setNextChapterId(next.getChapterId()));
    }

    /**
     * 转换为VO
     */
    private ChapterVO convertToVO(BookChapterEntity chapter) {
        ChapterVO vo = new ChapterVO();
        vo.setChapterId(chapter.getChapterId());
        vo.setBookId(chapter.getBookId());
        vo.setTitle(chapter.getTitle());
        vo.setChapterNumber(chapter.getChapterNumber());
        vo.setContent(chapter.getContent());
        vo.setWordCount(chapter.getWordCount());
        vo.setIsVip(chapter.getIsVip());
        vo.setIsPublished(chapter.getIsPublished());
        vo.setPrevChapterId(chapter.getPrevChapterId());
        vo.setNextChapterId(chapter.getNextChapterId());
        vo.setReadCount(chapter.getReadCount());
        vo.setCreatedAt(chapter.getCreatedAt());
        vo.setUpdatedAt(chapter.getUpdatedAt());
        return vo;
    }
}

