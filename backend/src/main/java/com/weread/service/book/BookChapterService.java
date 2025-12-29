package com.weread.service.book;

import com.weread.dto.book.ChapterCreateDTO;
import com.weread.entity.book.BookChapterEntity;
import com.weread.vo.book.ChapterVO;

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
}

