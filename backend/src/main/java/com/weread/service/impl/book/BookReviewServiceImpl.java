package com.weread.service.impl.book;

import com.weread.dto.book.BookReviewCreateDTO;
import com.weread.dto.book.BookReviewItemDTO;
import com.weread.dto.book.RecentBookReviewDTO;
import com.weread.dto.book.UserBookReviewsResponseDTO;
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

import java.time.format.DateTimeFormatter;
import java.util.List;
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

        // 允许同一用户对同一本书发表多个书评，不再检查是否已评价过

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
    public Integer deleteReview(Integer userId, Integer reviewId) {
        BookReviewEntity review = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "书评不存在"));

        // 检查权限：只能删除自己的书评
        if (!review.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权限删除此书评");
        }

        // 软删除：设置状态为3
        review.setStatus(3);
        bookReviewRepository.save(review);

        // 计算删除后用户剩余的书评数量（status=1的有效书评）
        Long remainingCount = bookReviewRepository.countByUserId(userId);
        return remainingCount != null ? remainingCount.intValue() : 0;
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

    @Override
    @Transactional(readOnly = true)
    public List<RecentBookReviewDTO> getUserRecentReviews(Integer userId, Integer limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<BookReviewEntity> reviews = bookReviewRepository.findByUserId(userId, pageable);

        return reviews.getContent().stream()
                .map(this::convertToRecentBookReviewDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserBookReviewsResponseDTO getUserReviewsWithCursor(Integer userId, Integer cursor, Integer limit) {
        // 参数验证和默认值设置
        if (limit == null || limit <= 0) {
            limit = 20; // 默认20条
        }
        if (limit > 50) {
            limit = 50; // 最大50条
        }

        // 创建分页对象（limit + 1，用于判断是否有更多数据）
        PageRequest pageable = PageRequest.of(0, limit + 1);

        // 查询书评列表
        List<BookReviewEntity> reviews;
        if (cursor == null) {
            // 第一页
            reviews = bookReviewRepository.findUserReviewsFirstPage(userId, pageable);
        } else {
            // 后续页
            reviews = bookReviewRepository.findUserReviewsByCursor(userId, cursor, pageable);
        }

        // 判断是否有更多数据
        boolean hasMore = reviews.size() > limit;
        if (hasMore) {
            reviews = reviews.subList(0, limit); // 只取前limit条
        }

        // 转换为DTO
        List<BookReviewItemDTO> reviewDTOs = reviews.stream()
                .map(this::convertToBookReviewItemDTO)
                .collect(Collectors.toList());

        // 计算nextCursor（如果有更多数据，使用最后一条书评的reviewId）
        // 根据接口定义，nextCursor是必填字段，类型为string，不能为null
        String nextCursor = "";
        if (hasMore && !reviews.isEmpty()) {
            nextCursor = String.valueOf(reviews.get(reviews.size() - 1).getReviewId());
        }

        // 获取总书评数
        Long totalCount = bookReviewRepository.countByUserId(userId);

        // 构建响应DTO
        UserBookReviewsResponseDTO response = new UserBookReviewsResponseDTO();
        response.setReviews(reviewDTOs);
        response.setHasMore(hasMore);
        response.setNextCursor(nextCursor); // 确保是string类型，不能为null
        response.setReviewCount(totalCount != null ? totalCount.intValue() : 0);

        return response;
    }

    /**
     * 转换为RecentBookReviewDTO（用于获取最新4条书评接口）
     */
    private RecentBookReviewDTO convertToRecentBookReviewDTO(BookReviewEntity review) {
        RecentBookReviewDTO dto = new RecentBookReviewDTO();
        
        // 加载书籍信息
        BookEntity book = bookRepository.findById(review.getBookId()).orElse(null);
        if (book != null) {
            dto.setCover(book.getCover() != null ? book.getCover() : "");
            dto.setBookTitle(book.getTitle() != null ? book.getTitle() : "");
        } else {
            dto.setCover("");
            dto.setBookTitle("");
        }

        // 设置书评日期（格式：YYYY-MM-DD）
        if (review.getCreatedAt() != null) {
            dto.setReviewDate(review.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE));
        } else {
            dto.setReviewDate("");
        }

        // 设置有用数（根据JSON定义minimum: 1，目前没有点赞功能，设置为1）
        dto.setHelpfulCount(1);

        // 设置书评内容
        dto.setReviewContent(review.getContent() != null ? review.getContent() : "");

        // 设置评分（保持原始值：recommend, average, not_recommend）
        if (review.getRating() != null) {
            dto.setRating(review.getRating().name().toLowerCase());
        } else {
            dto.setRating("average");
        }

        return dto;
    }

    /**
     * 转换为BookReviewItemDTO
     */
    private BookReviewItemDTO convertToBookReviewItemDTO(BookReviewEntity review) {
        BookReviewItemDTO dto = new BookReviewItemDTO();
        
        // 加载书籍信息
        BookEntity book = bookRepository.findById(review.getBookId()).orElse(null);
        if (book != null) {
            dto.setCover(book.getCover() != null ? book.getCover() : "");
            dto.setBookTitle(book.getTitle() != null ? book.getTitle() : "");
        } else {
            dto.setCover("");
            dto.setBookTitle("");
        }

        // 设置书评日期（格式：YYYY-MM-DD）
        if (review.getCreatedAt() != null) {
            dto.setReviewDate(review.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE));
        } else {
            dto.setReviewDate("");
        }

        // 设置有用数（目前没有点赞功能，设置为0）
        dto.setHelpfulCount(0);

        // 设置书评内容
        dto.setReviewContent(review.getContent() != null ? review.getContent() : "");

        // 设置评分（将 not_recommend 映射为 bad）
        if (review.getRating() != null) {
            String rating = review.getRating().name();
            if ("not_recommend".equals(rating)) {
                dto.setRating("bad");
            } else {
                dto.setRating(rating);
            }
        } else {
            dto.setRating("average");
        }

        return dto;
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
                        .ifPresent(author -> simpleBook.setAuthorName(author.getAuthorName()));
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

