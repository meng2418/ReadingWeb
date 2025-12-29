package com.weread.vo.bookmark;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 书签VO
 */
@Data
public class BookmarkVO {

    private Integer bookmarkId;

    private Integer bookId;

    private String bookTitle;

    private Integer chapterId;

    private String chapterTitle;

    private Integer pageNumber;

    private String note;

    private LocalDateTime createdAt;
}

