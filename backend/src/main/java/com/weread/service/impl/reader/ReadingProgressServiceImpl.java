package com.weread.service.impl.reader;

import com.weread.dto.reading.ReadingProgressResponse;
import com.weread.entity.book.ChapterEntity;
import com.weread.entity.book.ReadingProgressEntity;
import com.weread.repository.ReadingProgressRepository;
import com.weread.repository.book.BookRepository;
import com.weread.repository.book.ChapterRepository;
import com.weread.repository.bookshelf.BookshelfRepository;
import com.weread.service.reader.ReadingProgressService;
import com.weread.service.user.RecentBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReadingProgressServiceImpl implements ReadingProgressService {

    private final ReadingProgressRepository readingProgressRepository;
    private final ChapterRepository chapterRepository;
    private final BookRepository bookRepository;
    private final BookshelfRepository bookshelfRepository;
    private final RecentBookService recentBookService;

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

    @Override
    @Transactional
    public void updateReadingProgress(Integer bookId, Integer chapterId, Integer userId) {
        bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "图书不存在"));

        LocalDateTime now = LocalDateTime.now();
        ReadingProgressEntity progress = readingProgressRepository.findByUserIdAndBookId(userId, bookId)
                .orElseGet(() -> {
                    ReadingProgressEntity entity = new ReadingProgressEntity();
                    entity.setUserId(userId);
                    entity.setBookId(bookId);
                    return entity;
                });

        if (chapterId != null) {
            ChapterEntity chapter = chapterRepository.findByBookIdAndChapterId(bookId, chapterId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "章节不存在"));

            Long totalChapters = chapterRepository.countPublishedChaptersByBookId(bookId);
            float progressValue = 0f;
            if (totalChapters != null && totalChapters > 0 && chapter.getChapterNumber() != null) {
                progressValue = Math.min(1.0f, (float) chapter.getChapterNumber() / totalChapters);
            }
            progress.setChapterId(chapterId);
            progress.setProgress(progressValue);
        }

        progress.setLastReadAt(now);
        readingProgressRepository.save(progress);

        bookshelfRepository.findByUserIdAndBookId(userId, bookId)
                .ifPresent(shelf -> {
                    shelf.setLastReadAt(now);
                    bookshelfRepository.save(shelf);
                });

        recentBookService.recordReading(userId, bookId);
    }

    private void fillChapterInfo(ReadingProgressResponse response, ChapterEntity chapter) {
        response.setChapterTitle(chapter.getTitle());
        response.setChapterNumber(chapter.getChapterNumber() != null ? chapter.getChapterNumber() : 0);
    }
}