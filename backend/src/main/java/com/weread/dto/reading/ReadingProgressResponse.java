package com.weread.dto.reading;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReadingProgressResponse {
    private boolean hasProgress;
    private Integer chapterId;
    private LocalDateTime lastReadTime;
    private String chapterTitle;
    private Integer chapterNumber;
}