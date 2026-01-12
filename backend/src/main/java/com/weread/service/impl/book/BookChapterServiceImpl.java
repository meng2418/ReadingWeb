package com.weread.service.impl.book;

import com.weread.dto.book.ChapterCreateDTO;
import com.weread.dto.book.ChapterDTO;
import com.weread.entity.book.ChapterEntity;
import com.weread.entity.book.ReadingProgressEntity;
import com.weread.entity.book.BookEntity;
import com.weread.repository.book.ChapterRepository;
import com.weread.repository.book.BookRepository;
import com.weread.repository.ReadingProgressRepository;
import com.weread.service.asset.MemberService;
import com.weread.service.book.BookChapterService;
import com.weread.service.book.BookService;
import com.weread.vo.book.ChapterVO;
import com.weread.vo.book.ChapterContentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 章节服务实现类
 */
@Service
@RequiredArgsConstructor
public class BookChapterServiceImpl implements BookChapterService {

    private final ChapterRepository chapterRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final ReadingProgressRepository readingProgressRepository;
    private final MemberService memberService;

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
        ChapterEntity chapter = new ChapterEntity();
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
        ChapterEntity chapter = chapterRepository.findById(chapterId)
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
        ChapterEntity chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("章节不存在"));
        return convertToVO(chapter);
    }

    @Override
    public List<ChapterVO> getChaptersByBookId(Integer bookId) {
        List<ChapterEntity> chapters = chapterRepository.findByBookIdOrderByChapterNumberAsc(bookId);
        return chapters.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteChapter(Integer chapterId) {
        ChapterEntity chapter = chapterRepository.findById(chapterId)
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

    @Override
    public ChapterContentVO getChapterContent(Integer bookId, Integer chapterId, Integer userId) {
        // 修改这里：使用新的方法，一次性验证
        ChapterEntity chapter = chapterRepository.findByBookIdAndChapterId(bookId, chapterId)
            .orElseThrow(() -> new RuntimeException("章节不存在或不属于指定书籍"));

        // 创建ChapterContentVO
        ChapterContentVO vo = new ChapterContentVO();
        vo.setChapterId(chapter.getChapterId());
        vo.setChapterTitle(chapter.getTitle());
        vo.setChapterNumber(chapter.getChapterNumber());
        vo.setContent(chapter.getContent() != null ? chapter.getContent() : "");
        vo.setTotalWords(chapter.getWordCount() != null ? chapter.getWordCount() : 0);

        // 计算总页数和每页字数（假设每页500字）
        int wordsPerPage = 500;
        int totalWords = vo.getTotalWords();
        int totalPages = totalWords > 0 ? (int) Math.ceil((double) totalWords / wordsPerPage) : 1;
        vo.setWordsPerPage(wordsPerPage);
        vo.setTotalPages(totalPages);

        // 判断是否需要体验卡（VIP章节需要体验卡）
        vo.setRequireExperienceCard(chapter.getIsVip() != null && chapter.getIsVip());

        // 判断是否锁定（未发布的章节或VIP章节且用户不是会员时锁定）
        boolean isLocked = false;
        if (chapter.getIsPublished() == null || !chapter.getIsPublished()) {
            isLocked = true; // 未发布的章节锁定
        } else if (chapter.getIsVip() != null && chapter.getIsVip()) {
            // VIP章节需要检查用户会员状态
            if (userId != null) {
                // 检查用户是否是有效会员（包括正式会员和体验卡）
                boolean isMemberValid = memberService.isMemberValid(userId.longValue());
                isLocked = !isMemberValid; // 如果不是会员，则锁定
            } else {
                // 未登录用户，VIP章节锁定
                isLocked = true;
            }
        }
        vo.setIsLocked(isLocked);

        // 设置上一章和下一章ID（如果为null则返回0，符合接口定义）
        vo.setPrevChapterId(chapter.getPrevChapterId() != null ? chapter.getPrevChapterId() : 0);
        vo.setNextChapterId(chapter.getNextChapterId() != null ? chapter.getNextChapterId() : 0);

        // 获取最后阅读位置（如果有用户ID）
        Integer UserId = userId;
        int lastReadPosition = 0;
        if (userId != null) {
            Optional<ReadingProgressEntity> progressOpt = readingProgressRepository
                    .findByUserIdAndBookId(userId, bookId);
            if (progressOpt.isPresent()) {
                ReadingProgressEntity progress = progressOpt.get();
                // 如果当前章节是用户正在阅读的章节，使用currentPage计算位置
                if (progress.getChapterId() != null && progress.getChapterId().equals(chapterId)) {
                }
            }
        }
        vo.setLastReadPosition(lastReadPosition);

        return vo;
    }

    @Override
    public List<ChapterDTO> getBookChapters(Integer bookId) {
        // 获取书籍的所有章节，按章节序号排序
        List<ChapterEntity> chapters = chapterRepository.findByBookIdOrderByChapterNumberAsc(bookId);

        // 每页字数（与getChapterContent中的设置保持一致）
        int wordsPerPage = 500;

        // 计算每个章节的起始页码
        int currentPage = 1; // 第一页从1开始
        List<ChapterDTO> chapterDTOs = new java.util.ArrayList<>();

        for (ChapterEntity chapter : chapters) {
            ChapterDTO dto = new ChapterDTO();
            dto.setChapterId(chapter.getChapterId());  // 设置章节ID
            dto.setStartPage(currentPage);
            dto.setChapterNumber(chapter.getChapterNumber());
            dto.setChapterName(chapter.getTitle());

            chapterDTOs.add(dto);

            // 计算下一章的起始页码：当前页码 + 当前章节的页数
            int chapterWordCount = chapter.getWordCount() != null ? chapter.getWordCount() : 0;
            int chapterPages = chapterWordCount > 0 ? (int) Math.ceil((double) chapterWordCount / wordsPerPage) : 1;
            currentPage += chapterPages;
        }

        return chapterDTOs;
    }

    /**
     * 设置章节的上一章和下一章链接
     */
    private void setChapterLinks(ChapterEntity chapter) {
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
    private ChapterVO convertToVO(ChapterEntity chapter) {
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
