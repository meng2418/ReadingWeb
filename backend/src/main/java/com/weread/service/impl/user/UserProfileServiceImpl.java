package com.weread.service.impl.user;

import com.weread.dto.book.RecentBookReviewDTO;
import com.weread.dto.note.BookNoteDTO;
import com.weread.entity.book.BookEntity;
import com.weread.entity.bookshelf.BookshelfEntity;
import com.weread.repository.book.BookRepository;
import com.weread.repository.bookshelf.BookshelfRepository;
import com.weread.repository.user.FollowRepository;
import com.weread.service.book.BookReviewService;
import com.weread.service.note.NoteService;
import com.weread.service.user.UserPrivacySettingsService;
import com.weread.service.user.UserProfileService;
import com.weread.service.user.UserService;
import com.weread.vo.user.BookReviewProfileVO;
import com.weread.vo.user.HighlightProfileVO;
import com.weread.vo.user.HighlightVO;
import com.weread.vo.user.ProfileBookshelfPageVO;
import com.weread.vo.user.RecentBookProfileVO;
import com.weread.vo.user.ThoughtProfileVO;
import com.weread.vo.user.UserPrivacyVisibilityVO;
import com.weread.vo.user.UserProfileOtherVO;
import com.weread.vo.user.UserProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserService userService;
    private final UserPrivacySettingsService privacySettingsService;
    private final FollowRepository followRepository;
    private final BookshelfRepository bookshelfRepository;
    private final BookRepository bookRepository;
    private final NoteService noteService;
    private final BookReviewService bookReviewService;

    @Override
    @Transactional(readOnly = true)
    public UserProfileOtherVO getUserProfile(Integer targetUserId, Integer currentUserId, Integer page, Integer limit) {
        UserProfileVO base = userService.getUserHome(targetUserId);
        boolean isSelf = currentUserId != null && currentUserId.equals(targetUserId);

        UserProfileOtherVO result = new UserProfileOtherVO();
        result.setAvatar(base.getAvatar());
        result.setUsername(base.getUsername());
        result.setBio(base.getBio());
        result.setFollowingCount(base.getFollowingCount());
        result.setFollowerCount(base.getFollowerCount());
        result.setPostCount(base.getPostCount());
        result.setIsMember(base.getIsMember());
        result.setConsecutiveReadingDays(base.getConsecutiveReadingDays());
        result.setIsSelf(isSelf);

        UserPrivacyVisibilityVO visibility = privacySettingsService.getVisibility(targetUserId);
        result.setVisibility(visibility);

        boolean canView = isSelf;

        if (canView || Boolean.TRUE.equals(visibility.getReadingStats())) {
            result.setReadingStats(base.getReadingStats());
        }
        if (!canView && !Boolean.TRUE.equals(visibility.getFollowers())) {
            result.setFollowerCount(null);
        }
        if (!canView && !Boolean.TRUE.equals(visibility.getFollowing())) {
            result.setFollowingCount(null);
        }

        if (currentUserId != null && !isSelf) {
            result.setIsFollowing(
                    followRepository.findByFollowerIdAndFollowingId(currentUserId, targetUserId).isPresent());
            result.setIsFollower(
                    followRepository.findByFollowerIdAndFollowingId(targetUserId, currentUserId).isPresent());
        }

        if (canView || Boolean.TRUE.equals(visibility.getBookshelf())) {
            result.setBookshelf(buildBookshelfPage(targetUserId, page, limit));
        }
        if (canView || Boolean.TRUE.equals(visibility.getHighlights())) {
            result.setHighlights(buildHighlights(targetUserId));
        }
        if (canView || Boolean.TRUE.equals(visibility.getThoughts())) {
            result.setThoughts(buildThoughts(targetUserId));
        }
        if (canView || Boolean.TRUE.equals(visibility.getBookReviews())) {
            result.setBookReviews(buildBookReviews(targetUserId));
        }

        return result;
    }

    private ProfileBookshelfPageVO buildBookshelfPage(Integer targetUserId, Integer page, Integer limit) {
        int safePage = (page == null || page < 1) ? 1 : page;
        int safeLimit = (limit == null || limit < 1) ? 10 : Math.min(limit, 10);

        long totalLong = bookshelfRepository.countByUserId(targetUserId);
        int total = totalLong > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) totalLong;

        Page<BookshelfEntity> shelfPage = bookshelfRepository.findByUserId(
                targetUserId,
                PageRequest.of(safePage - 1, safeLimit, Sort.by(Sort.Direction.DESC, "lastReadAt", "addedAt")));

        List<BookshelfEntity> rows = shelfPage.getContent();
        ProfileBookshelfPageVO pageVO = new ProfileBookshelfPageVO();
        pageVO.setTotal(total);
        pageVO.setPage(safePage);
        pageVO.setLimit(safeLimit);
        pageVO.setHasMore((long) safePage * safeLimit < totalLong);

        if (rows.isEmpty()) {
            pageVO.setItems(Collections.emptyList());
            return pageVO;
        }

        List<Integer> bookIds = rows.stream().map(BookshelfEntity::getBookId).distinct().toList();
        Map<Integer, BookEntity> bookMap = bookRepository.findByBookIds(bookIds).stream()
                .collect(Collectors.toMap(BookEntity::getBookId, Function.identity(), (a, b) -> a));

        List<RecentBookProfileVO> items = rows.stream().map(row -> {
            RecentBookProfileVO item = new RecentBookProfileVO();
            item.setBookId(row.getBookId());
            item.setReadingStatus(row.getStatus());
            BookEntity book = bookMap.get(row.getBookId());
            if (book != null) {
                item.setTitle(book.getTitle() != null ? book.getTitle() : "");
                item.setCover(book.getCover() != null ? book.getCover() : "");
            } else {
                item.setTitle("");
                item.setCover("");
            }
            return item;
        }).toList();

        pageVO.setItems(items);
        return pageVO;
    }

    private List<HighlightProfileVO> buildHighlights(Integer targetUserId) {
        return noteService.getUserRecentHighlights3(targetUserId).stream()
                .map(this::toHighlightProfileVO)
                .toList();
    }

    private HighlightProfileVO toHighlightProfileVO(HighlightVO vo) {
        HighlightProfileVO item = new HighlightProfileVO();
        item.setBookName(vo.getBookTitle() != null ? vo.getBookTitle() : "");
        item.setText(vo.getContent() != null ? vo.getContent() : "");
        item.setChapter(vo.getChapterName() != null ? vo.getChapterName() : "");
        item.setDate(vo.getHighlightDate() != null ? vo.getHighlightDate().toString() : "");
        return item;
    }

    private List<ThoughtProfileVO> buildThoughts(Integer targetUserId) {
        return noteService.getUserRecentNotes6(targetUserId).stream()
                .map(this::toThoughtProfileVO)
                .toList();
    }

    private ThoughtProfileVO toThoughtProfileVO(BookNoteDTO dto) {
        ThoughtProfileVO item = new ThoughtProfileVO();
        item.setBookName(dto.getBookTitle() != null ? dto.getBookTitle() : "");
        item.setThought(dto.getNoteContent() != null ? dto.getNoteContent() : "");
        item.setQuote(dto.getQuote() != null ? dto.getQuote() : "");
        item.setDate(dto.getNoteDate() != null ? dto.getNoteDate() : "");
        return item;
    }

    private List<BookReviewProfileVO> buildBookReviews(Integer targetUserId) {
        return bookReviewService.getUserRecentReviews(targetUserId, 4).stream()
                .map(this::toBookReviewProfileVO)
                .toList();
    }

    private BookReviewProfileVO toBookReviewProfileVO(RecentBookReviewDTO dto) {
        BookReviewProfileVO item = new BookReviewProfileVO();
        item.setBookName(dto.getBookTitle() != null ? dto.getBookTitle() : "");
        item.setCover(dto.getCover() != null ? dto.getCover() : "");
        item.setRating(dto.getRating() != null ? dto.getRating() : "recommend");
        item.setDate(dto.getReviewDate() != null ? dto.getReviewDate() : "");
        item.setLikes(dto.getHelpfulCount() != null ? dto.getHelpfulCount() : 0);
        item.setContent(dto.getReviewContent() != null ? dto.getReviewContent() : "");
        return item;
    }
}
