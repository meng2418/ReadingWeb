package com.weread.service.reader;

import com.weread.dto.reading.ReadingProgressResponse;

public interface ReadingProgressService {

    ReadingProgressResponse getReadingProgress(Integer bookId, Integer userId);

    void updateReadingProgress(Integer bookId, Integer chapterId, Integer userId);
}