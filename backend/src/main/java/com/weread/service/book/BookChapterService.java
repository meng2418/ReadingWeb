package com.weread.service.book;

import com.weread.dto.book.ChapterCreateDTO;
import com.weread.dto.book.ChapterDTO;
import com.weread.vo.book.ChapterVO;
import com.weread.vo.book.ChapterContentVO;

import java.util.List;

/**
 * 章节服务接口
 */
public interface BookChapterService {

    /**
     * 创建章节
     */
    ChapterVO createChapter(ChapterCreateDTO dto);

    /**
     * 更新章节
     */
    ChapterVO updateChapter(Integer chapterId, ChapterCreateDTO dto);

    /**
     * 根据ID获取章节
     */
    ChapterVO getChapterById(Integer chapterId);

    /**
     * 根据书籍ID获取所有章节
     */
    List<ChapterVO> getChaptersByBookId(Integer bookId);

    /**
     * 删除章节
     */
    void deleteChapter(Integer chapterId);

    /**
     * 获取上一章
     */
    ChapterVO getPrevChapter(Integer bookId, Integer chapterNumber);

    /**
     * 获取下一章
     */
    ChapterVO getNextChapter(Integer bookId, Integer chapterNumber);

    /**
     * 计算章节字数
     */
    Integer calculateWordCount(String content);

    /**
     * 获取章节内容和信息（用于阅读器）
     * @param bookId 书籍ID
     * @param chapterId 章节ID
     * @param userId 用户ID（可为null，用于获取阅读进度）
     * @return 章节内容VO
     */
    ChapterContentVO getChapterContent(Integer bookId, Integer chapterId, Integer userId);

    /**
     * 获取书籍目录列表（用于阅读器）
     * @param bookId 书籍ID
     * @return 章节目录列表，包含起始页码、章节序号和章节名称
     */
    List<ChapterDTO> getBookChapters(Integer bookId);
}

