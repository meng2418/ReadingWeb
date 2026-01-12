package com.weread.service.impl.book;

import com.weread.dto.book.BookCreateDTO;
import com.weread.dto.book.BookQueryDTO;
import com.weread.dto.book.BookUpdateDTO;
import com.weread.entity.author.AuthorEntity;
import com.weread.entity.book.CategoryEntity;
import com.weread.entity.book.ReadingProgressEntity;
import com.weread.entity.bookshelf.BookshelfEntity;
import com.weread.entity.book.BookEntity;
import com.weread.repository.author.AuthorRepository;
import com.weread.repository.book.BookCategoryRepository;
import com.weread.repository.book.ChapterRepository;
import com.weread.repository.book.BookRepository;
import com.weread.repository.book.BookReviewRepository;
import com.weread.repository.ReadingProgressRepository;
import com.weread.repository.bookshelf.BookshelfRepository;
import com.weread.service.book.BookService;
import com.weread.service.book.BookReviewService;
import com.weread.vo.book.AuthorWorkVO;
import com.weread.vo.book.BookDetailVO;
import com.weread.vo.book.BookListVO;
import com.weread.vo.book.BookSummaryVO;
import com.weread.vo.book.RelatedBookVO;
import com.weread.util.ImagePathUtils;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 书籍服务实现类
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookCategoryRepository categoryRepository;
    private final ChapterRepository chapterRepository;
    private final BookshelfRepository bookshelfRepository;
    private final ReadingProgressRepository readingProgressRepository;
    private final BookReviewRepository bookReviewRepository;
    private final BookReviewService bookReviewService;

    @Override
    public BookSummaryVO convertToSummaryVO(BookEntity bookEntity) {
        BookSummaryVO vo = new BookSummaryVO();
        vo.setBookId(bookEntity.getBookId());
        vo.setTitle(bookEntity.getTitle());
        vo.setCover(ImagePathUtils.processCoverPath(bookEntity.getCover()));
        vo.setDescription(bookEntity.getDescription());

        if (bookEntity.getAuthor() != null) {
            vo.setAuthorName(bookEntity.getAuthor().getAuthorName());
        }

        return vo;
    }

    @Override
    @Transactional
    public BookDetailVO createBook(BookCreateDTO dto) {
        // 验证作者是否存在
        AuthorEntity author = authorRepository.findByAuthorId(dto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("作者不存在"));

        // 验证分类是否存在
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("分类不存在"));

        // 创建书籍实体
        BookEntity book = new BookEntity();
        book.setTitle(dto.getTitle());
        book.setAuthorId(dto.getAuthorId());
        book.setCover(dto.getCover());
        book.setDescription(dto.getDescription());
        book.setCategoryId(dto.getCategoryId());
        book.setPublisher(dto.getPublisher());
        book.setIsbn(dto.getIsbn());
        book.setPrice(dto.getPrice() != null ? dto.getPrice() : 0);
        book.setIsbn(dto.getIsbn() != null ? dto.getIsbn() : false);
        book.setIsMemberOnly(dto.getIsMemberOnly() != null ? dto.getIsMemberOnly() : false);
        book.setTopics(dto.getTopics());
        book.setIsPublished(true); // 默认上架

        book = bookRepository.save(book);
        return convertToDetailVO(book);
    }

    @Override
    @Transactional
    public BookDetailVO updateBook(Integer bookId, BookUpdateDTO dto) {
        BookEntity book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("书籍不存在"));

        // 更新字段
        if (dto.getTitle() != null) {
            book.setTitle(dto.getTitle());
        }
        if (dto.getAuthorId() != null) {
            AuthorEntity author = authorRepository.findByAuthorId(dto.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("作者不存在"));
            book.setAuthorId(dto.getAuthorId());
        }
        if (dto.getCover() != null) {
            book.setCover(dto.getCover());
        }
        if (dto.getDescription() != null) {
            book.setDescription(dto.getDescription());
        }
        if (dto.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("分类不存在"));
            book.setCategoryId(dto.getCategoryId());
        }
        if (dto.getPublisher() != null) {
            book.setPublisher(dto.getPublisher());
        }
        if (dto.getIsbn() != null) {
            book.setIsbn(dto.getIsbn());
        }
        if (dto.getPrice() != null) {
            book.setPrice(dto.getPrice());
        }
        if (dto.getIsbn() != null) {
            book.setIsbn(dto.getIsbn());
        }
        if (dto.getIsPublished() != null) {
            book.setIsPublished(dto.getIsPublished());
        }
        if (dto.getIsMemberOnly() != null) {
            book.setIsMemberOnly(dto.getIsMemberOnly());
        }
        if (dto.getTopics() != null) {
            book.setTopics(dto.getTopics());
        }

        book = bookRepository.save(book);
        return convertToDetailVO(book);
    }

    @Override
    public BookDetailVO getBookById(Integer bookId) {
        return getBookById(bookId, null);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDetailVO getBookById(Integer bookId, Integer userId) {
        BookEntity book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("书籍不存在"));
        return convertToDetailVO(book, userId);
    }

    @Override
    @Transactional
    public void deleteBook(Integer bookId) {
        BookEntity book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("书籍不存在"));
        book.setIsPublished(false); // 软删除，设置为下架
        bookRepository.save(book);
    }

    @Override
    public Page<BookListVO> searchBooks(BookQueryDTO queryDTO) {
        Pageable pageable = createPageable(queryDTO.getPage(), queryDTO.getSize(),
                queryDTO.getSortBy(), queryDTO.getSortOrder());

        Page<BookEntity> bookPage;

        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().trim().isEmpty()) {
            // 关键词搜索
            bookPage = bookRepository.searchBooks(queryDTO.getKeyword().trim(), pageable);
        } else {
            // 条件筛选
            bookPage = bookRepository.findBooksWithFilters(
                    queryDTO.getCategoryId(),
                    queryDTO.getIsFree(),
                    queryDTO.getIsMemberOnly(),
                    pageable);
        }

        return bookPage.map(this::convertToListVO);
    }

    @Override
    public Page<BookListVO> getPopularBooks(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookEntity> bookPage = bookRepository.findPopularBooks(pageable);
        return bookPage.map(this::convertToListVO);
    }

    @Override
    public Page<BookListVO> getBooksByCategory(Integer categoryId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookEntity> bookPage = bookRepository.findByCategoryIdAndIsPublishedTrue(categoryId, pageable);
        return bookPage.map(this::convertToListVO);
    }

    @Override
    @Transactional
    public void updateBookStatus(Integer bookId, Boolean isPublished) {
        BookEntity book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("书籍不存在"));
        book.setIsPublished(isPublished);
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void updateBookWordCount(Integer bookId) {
        Integer totalWordCount = chapterRepository.sumWordCountByBookId(bookId);
        BookEntity book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("书籍不存在"));
        book.setWordCount(totalWordCount != null ? totalWordCount : 0);
        bookRepository.save(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorWorkVO> getAuthorRepresentativeWorks(Integer bookId) {
        // 1. 查询书籍信息
        BookEntity book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("书籍不存在，ID: " + bookId));

        // 2. 获取作者ID
        Integer authorId = book.getAuthorId();
        if (authorId == null) {
            return List.of(); // 如果没有作者，返回空列表
        }

        // 3. 查询该作者的其他作品（排除当前书籍，按阅读量排序，最多3部）
        Long AuthorId = authorId.longValue();
        Pageable pageable = PageRequest.of(0, 3);
        List<BookEntity> works = bookRepository.findAuthorRepresentativeWorks(AuthorId, bookId, pageable);

        // 4. 转换为VO
        return works.stream()
                .map(this::convertToAuthorWorkVO)
                .collect(Collectors.toList());
    }

    /**
     * 将 BookEntity 转换为 AuthorWorkVO
     */
    private AuthorWorkVO convertToAuthorWorkVO(BookEntity book) {
        AuthorWorkVO vo = new AuthorWorkVO();
        // 将 bookId 转换为 String 类型
        vo.setBookId(book.getBookId() != null ? String.valueOf(book.getBookId()) : "");
        vo.setCover(ImagePathUtils.processCoverPath(book.getCover()));
        vo.setBookTitle(book.getTitle());
        return vo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelatedBookVO> getRelatedBooks(Integer bookId) {
        // 1. 查询书籍信息
        BookEntity book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("书籍不存在，ID: " + bookId));

        // 2. 获取分类ID
        Integer categoryId = book.getCategoryId();
        if (categoryId == null) {
            return List.of(); // 如果没有分类，返回空列表
        }

        // 3. 查询同分类的其他书籍（排除当前书籍，按阅读量和评分排序，最多3部）
        Pageable pageable = PageRequest.of(0, 3);
        List<BookEntity> relatedBooks = bookRepository.findRelatedBooksByCategory(categoryId, bookId, pageable);

        // 4. 转换为VO
        return relatedBooks.stream()
                .map(this::convertToRelatedBookVO)
                .collect(Collectors.toList());
    }

    /**
     * 将 BookEntity 转换为 RelatedBookVO
     */
    private RelatedBookVO convertToRelatedBookVO(BookEntity book) {
        RelatedBookVO vo = new RelatedBookVO();
        vo.setBookId(book.getBookId());
        vo.setCover(ImagePathUtils.processCoverPath(book.getCover()));
        vo.setTitle(book.getTitle());
        vo.setDescription(book.getDescription() != null ? book.getDescription() : "");

        // 作者名
        if (book.getAuthor() != null) {
            vo.setAuthorName(book.getAuthor().getAuthorName());
        } else {
            vo.setAuthorName("");
        }

        // 评分
        vo.setRating(book.getRating() != null ? book.getRating() : 0f);

        // 阅读量
        vo.setReadCount(book.getReadCount() != null ? book.getReadCount() : 0);

        // 价格
        vo.setPrice(book.getPrice() != null ? book.getPrice() : 0);

        // 是否免费
        vo.setIsFree(book.getIsFree() != null ? book.getIsFree() : false);

        return vo;
    }

    /**
     * 转换为详情VO（不包含用户相关状态）
     */
    private BookDetailVO convertToDetailVO(BookEntity book) {
        return convertToDetailVO(book, null);
    }

    /**
     * 转换为详情VO（包含用户相关状态和统计信息）
     */
    private BookDetailVO convertToDetailVO(BookEntity book, Integer userId) {
        BookDetailVO vo = new BookDetailVO();

        // 基础信息
        vo.setBookId(book.getBookId());
        vo.setTitle(book.getTitle());
        vo.setBookTitle(book.getTitle()); // 前端期望的字段名
        vo.setAuthorId(book.getAuthorId());

        // 作者信息
        if (book.getAuthor() != null) {
            vo.setAuthorName(book.getAuthor().getAuthorName());
            vo.setAuthor(book.getAuthor().getAuthorName()); // 前端期望的字段名
            // authorBio 确保是 string 类型，如果为 null 则返回空字符串
            String bio = book.getAuthor().getBio();
            vo.setAuthorBio(bio != null ? bio : "");
        } else {
            // 如果没有作者信息，设置默认值
            vo.setAuthorBio("");
        }

        vo.setCover(ImagePathUtils.processCoverPath(book.getCover()));
        vo.setDescription(book.getDescription());
        vo.setCategoryId(book.getCategoryId());

        // 获取分类名称
        Optional<CategoryEntity> category = categoryRepository.findById(book.getCategoryId());
        category.ifPresent(c -> vo.setCategoryName(c.getName()));

        vo.setPublisher(book.getPublisher());
        vo.setPublishDate(book.getPublishDate());
        vo.setIsbn(book.getIsbn());
        vo.setWordCount(book.getWordCount());
        vo.setPrice(book.getPrice());
        vo.setIsFree(book.getIsFree());
        vo.setIsPublished(book.getIsPublished());
        vo.setIsMemberOnly(book.getIsMemberOnly());
        vo.setIsFreeForMember(book.getIsMemberOnly()); // 体验卡是否可读
        vo.setTags(book.getTags());
        // 将 Float rating 转换为 Integer（0-100范围）
        // 如果rating是0-1的小数格式，转换为0-100的整数；如果已经是0-100的值，直接取整
        if (book.getRating() != null) {
            if (book.getRating() <= 1.0f) {
                // 0-1的小数格式，转换为0-100的整数
                vo.setRating(Math.round(book.getRating() * 100));
            } else {
                // 已经是0-100的值，直接取整
                vo.setRating(Math.round(book.getRating()));
            }
            // 确保不超过100
            if (vo.getRating() > 100) {
                vo.setRating(100);
            }
        } else {
            vo.setRating(0);
        }
        vo.setReadCount(book.getReadCount());
        vo.setCreatedAt(book.getCreatedAt());
        vo.setUpdatedAt(book.getUpdatedAt());

        // 用户相关状态（如果提供了userId）
        Long UserId = userId != null ? userId.longValue() : null;
        if (userId != null) {
            // 检查是否在书架中
            Optional<BookshelfEntity> bookshelf = bookshelfRepository.findByUserIdAndBookId(UserId, book.getBookId());
            vo.setIsInBookshelf(bookshelf.isPresent());

            // 检查阅读进度
            Optional<ReadingProgressEntity> progress = readingProgressRepository.findByUserIdAndBookId(userId,
                    book.getBookId());
            if (progress.isPresent()) {
                ReadingProgressEntity progressEntity = progress.get();
                Float progressValue = progressEntity.getProgress();
                vo.setHasStarted(progressValue != null && progressValue > 0);

                // 判断阅读状态
                if (progressValue == null || progressValue == 0) {
                    vo.setReadingStatus("not_started");
                    vo.setIsFinished(false);
                } else if (progressValue >= 1.0f) {
                    vo.setReadingStatus("finished");
                    vo.setIsFinished(true);
                } else {
                    vo.setReadingStatus("reading");
                    vo.setIsFinished(false);
                }
            } else {
                vo.setHasStarted(false);
                vo.setReadingStatus("not_started");
                vo.setIsFinished(false);
            }
        } else {
            // 未登录用户，设置默认值
            vo.setIsInBookshelf(false);
            vo.setHasStarted(false);
            vo.setReadingStatus("not_started");
            vo.setIsFinished(false);
        }

        // 统计信息
        Long readingCount = readingProgressRepository.countReadingUsers(book.getBookId());
        Long finishedCount = readingProgressRepository.countFinishedUsers(book.getBookId());
        vo.setReadingCount(readingCount != null ? readingCount.intValue() : 0);
        vo.setFinishedCount(finishedCount != null ? finishedCount.intValue() : 0);

        // 点评统计
        Long ratingCount = bookReviewRepository.countByBookId(book.getBookId());
        vo.setRatingCount(ratingCount != null ? ratingCount.intValue() : 0);

        // 点评详情
        BookReviewService.RatingStats ratingStats = bookReviewService.getRatingStats(book.getBookId());
        BookDetailVO.RatingDetailVO ratingDetail = new BookDetailVO.RatingDetailVO();
        long total = ratingStats.getTotal();
        if (total > 0) {
            ratingDetail.setRecommendPercent((ratingStats.getRecommend() * 100.0) / total);
            ratingDetail.setAveragePercent((ratingStats.getAverage() * 100.0) / total);
            ratingDetail.setNotRecommendPercent((ratingStats.getNotRecommend() * 100.0) / total);
        } else {
            ratingDetail.setRecommendPercent(0.0);
            ratingDetail.setAveragePercent(0.0);
            ratingDetail.setNotRecommendPercent(0.0);
        }
        vo.setRatingDetail(ratingDetail);

        return vo;
    }

    /**
     * 转换为列表VO
     */
    private BookListVO convertToListVO(BookEntity book) {
        BookListVO vo = new BookListVO();
        vo.setBookId(book.getBookId());
        vo.setTitle(book.getTitle());
        if (book.getAuthor() != null) {
            vo.setAuthorName(book.getAuthor().getAuthorName());
        }
        vo.setCover(ImagePathUtils.processCoverPath(book.getCover()));
        vo.setDescription(book.getDescription());
        vo.setCategoryId(book.getCategoryId());

        // 获取分类名称
        Optional<CategoryEntity> category = categoryRepository.findById(book.getCategoryId());
        category.ifPresent(c -> vo.setCategoryName(c.getName()));

        vo.setPrice(book.getPrice());
        vo.setIsFree(book.getIsFree());
        vo.setIsMemberOnly(book.getIsMemberOnly());
        vo.setTags(book.getTags());
        vo.setRating(book.getRating());
        vo.setReadCount(book.getReadCount());
        vo.setCreatedAt(book.getCreatedAt());

        return vo;
    }

    /**
     * 创建分页对象
     */
    private Pageable createPageable(Integer page, Integer size, String sortBy, String sortOrder) {
        page = page != null && page >= 0 ? page : 0;
        size = size != null && size > 0 ? size : 20;
        sortBy = sortBy != null ? sortBy : "createdAt";
        sortOrder = sortOrder != null && sortOrder.equalsIgnoreCase("ASC") ? "ASC" : "DESC";

        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        return PageRequest.of(page, size, sort);
    }
}
