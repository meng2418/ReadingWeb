package com.weread.dto.reading;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReadingProgressResponse {
    private boolean hasProgress;
    private Integer chapterId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime lastReadTime;

    private String chapterTitle;
    private Integer chapterNumber;
}