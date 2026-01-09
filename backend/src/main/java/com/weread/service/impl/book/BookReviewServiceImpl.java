package com.weread.service.impl.book;

import com.weread.dto.book.BookReviewCreateDTO;
import com.weread.entity.BookEntity;
import com.weread.entity.book.BookReviewEntity;
import com.weread.entity.user.UserEntity;
import com.weread.repository.AuthorRepository;
import com.weread.repository.BookRepository;
import com.weread.repository.book.BookReviewRepository;
import com.weread.repository.user.UserRepository;
import com.weread.service.book.BookReviewService;
import com.weread.vo.book.BookReviewVO;
import com.weread.vo.book.SimpleBookVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 书评服务实现类
 */
@Service
@RequiredArgsConstructor
public class BookReviewServiceImpl implements BookReviewService {

    private final BookReviewRepository bookReviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;

    @Override
    @Transactional
    public BookReviewVO createReview(Integer userId, BookReviewCreateDTO dto) {
        // 验证书籍是否存在
        BookEntity book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "书籍不存在"));

        // 检查用户是否已经评价过
        Optional<BookReviewEntity> existingReview = bookReviewRepository
                .findByBookIdAndUserIdAndStatus(dto.getBookId(), userId, 1);
        if (existingReview.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "您已经评价过此书，无法重复评价");
        }

        // 验证评分值
        BookReviewEntity.Rating rating;
        try {
            rating = BookReviewEntity.Rating.valueOf(dto.getRating());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "评分值无效，必须是 recommend、average 或 not_recommend");
        }

        // 创建书评
        BookReviewEntity review = new BookReviewEntity();
        review.setBookId(dto.getBookId());
        review.setUserId(userId);
        review.setRating(rating);
        review.setContent(dto.getContent());
        review.setIsPublic(dto.getIsPublic() != null ? dto.getIsPublic() : true);
        review.setStatus(1);

        review = bookReviewRepository.save(review);

        // 加载用户信息
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

        return convertToVO(review, book, user);
    }

    @Override
    @Transactional
    public void deleteReview(Integer userId, Integer reviewId) {
        BookReviewEntity review = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "书评不存在"));

        // 检查权限：只能删除自己的书评
        if (!review.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权限删除此书评");
        }

        // 软删除：设置状态为3
        review.setStatus(3);
        bookReviewRepository.save(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookReviewVO> getBookReviews(Integer bookId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookReviewEntity> reviews = bookReviewRepository.findByBookId(bookId, pageable);

        return reviews.map(this::convertToVO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookReviewVO> getBookReviewsLimited(Integer bookId, Integer limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<BookReviewEntity> reviews = bookReviewRepository.findByBookId(bookId, pageable);

        return reviews.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RatingStats getRatingStats(Integer bookId) {
        List<Object[]> stats = bookReviewRepository.countRatingsByBookId(bookId);
        RatingStats ratingStats = new RatingStats();

        for (Object[] stat : stats) {
            BookReviewEntity.Rating rating = (BookReviewEntity.Rating) stat[0];
            Long count = (Long) stat[1];

            switch (rating) {
                case recommend:
                    ratingStats.setRecommend(count);
                    break;
                case average:
                    ratingStats.setAverage(count);
                    break;
                case not_recommend:
                    ratingStats.setNotRecommend(count);
                    break;
            }
        }

        ratingStats.setTotal(ratingStats.getRecommend() + ratingStats.getAverage() + ratingStats.getNotRecommend());
        return ratingStats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookReviewVO> getUserReviewsLimited(Integer userId, Integer limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<BookReviewEntity> reviews = bookReviewRepository.findByUserId(userId, pageable);

        return reviews.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO（需要加载关联实体）
     */
    private BookReviewVO convertToVO(BookReviewEntity review) {
        BookEntity book = bookRepository.findById(review.getBookId()).orElse(null);
        UserEntity user = userRepository.findById(review.getUserId()).orElse(null);
        return convertToVO(review, book, user);
    }

    /**
     * 转换为VO
     */
    private BookReviewVO convertToVO(BookReviewEntity review, BookEntity book, UserEntity user) {
        BookReviewVO vo = new BookReviewVO();
        vo.setReviewId(review.getReviewId());
        vo.setBookId(review.getBookId());
        vo.setUserId(review.getUserId());
        vo.setRating(review.getRating().name());
        vo.setContent(review.getContent());
        vo.setIsPublic(review.getIsPublic());
        vo.setCreatedAt(review.getCreatedAt());
        vo.setReviewTime(review.getCreatedAt()); // 别名

        if (book != null) {
            vo.setBookTitle(book.getTitle());
            vo.setBookCover(book.getCover());
            
            // 填充book对象（SimpleBook格式）
            SimpleBookVO simpleBook = new SimpleBookVO();
            simpleBook.setCover(book.getCover());
            simpleBook.setBookTitle(book.getTitle());
            simpleBook.setAuthorId(book.getAuthorId());
            simpleBook.setDescription(book.getDescription());
            // 将Float rating转换为Integer（0-100范围）
            if (book.getRating() != null) {
                if (book.getRating() <= 1.0f) {
                    // 如果是0-1的小数格式，转换为0-100的整数
                    simpleBook.setRating((int) (book.getRating() * 100));
                } else {
                    // 如果已经是0-100的值，直接取整
                    simpleBook.setRating(book.getRating().intValue());
                }
            } else {
                simpleBook.setRating(0);
            }
            simpleBook.setReadCount(book.getReadCount() != null ? book.getReadCount() : 0);
            
            // 设置作者名（通过authorRepository查询，避免LazyInitializationException）
            if (book.getAuthorId() != null) {
                authorRepository.findById(book.getAuthorId())
                        .ifPresent(author -> simpleBook.setAuthorName(author.getName()));
            }
            
            vo.setBook(simpleBook);
        }

        if (user != null) {
            vo.setUsername(user.getUsername());
            // 确保 avatar 始终是 string 类型，如果为 null 则返回空字符串
            vo.setAvatar(user.getAvatar() != null ? user.getAvatar() : "");
        } else {
            // 如果用户不存在，设置默认值
            vo.setAvatar("");
        }

        return vo;
    }
}

