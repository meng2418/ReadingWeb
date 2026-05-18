package com.weread.service.impl.reader;

import com.weread.dto.reading.ReadingProgressResponse;
import com.weread.entity.book.ChapterEntity;
import com.weread.entity.book.ReadingProgressEntity;
import com.weread.repository.ReadingProgressRepository;
import com.weread.repository.book.ChapterRepository;
import com.weread.service.reader.ReadingProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReadingProgressServiceImpl implements ReadingProgressService {

    private final ReadingProgressRepository readingProgressRepository;
    private final ChapterRepository chapterRepository;

    @Override
    public ReadingProgressResponse getReadingProgress(Integer bookId, Integer userId) {
        ReadingProgressResponse response = new ReadingProgressResponse();
        response.setHasProgress(false);
        response.setChapterId(0);
        response.setChapterNumber(0);
        response.setChapterTitle("");

        Optional<ReadingProgressEntity> progressOpt =
                readingProgressRepository.findByUserIdAndBookId(userId, bookId);

        if (progressOpt.isEmpty()) {
            return response;
        }

        ReadingProgressEntity progress = progressOpt.get();
        boolean hasProgress = progress.getChapterId() != null
                || (progress.getProgress() != null && progress.getProgress() > 0);

        if (!hasProgress) {
            return response;
        }

        response.setHasProgress(true);
        response.setLastReadTime(progress.getLastReadAt());

        if (progress.getChapterId() != null) {
            response.setChapterId(progress.getChapterId());
            chapterRepository.findByBookIdAndChapterId(bookId, progress.getChapterId())
                    .ifPresent(chapter -> fillChapterInfo(response, chapter));
        }

        return response;
    }

    private void fillChapterInfo(ReadingProgressResponse response, ChapterEntity chapter) {
        response.setChapterTitle(chapter.getTitle());
        response.setChapterNumber(chapter.getChapterNumber() != null ? chapter.getChapterNumber() : 0);
    }
}