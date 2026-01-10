package com.weread.service.impl.book;

import com.weread.dto.book.BookCreateDTO;
import com.weread.dto.book.BookQueryDTO;
import com.weread.dto.book.BookUpdateDTO;
import com.weread.entity.AuthorEntity;
import com.weread.entity.BookEntity;
import com.weread.entity.book.BookCategoryEntity;
import com.weread.repository.AuthorRepository;
import com.weread.repository.BookRepository;
import com.weread.repository.book.BookCategoryRepository;
import com.weread.repository.book.BookChapterRepository;
import com.weread.service.book.BookService;
import com.weread.vo.book.BookDetailVO;
import com.weread.vo.book.BookListVO;
import com.weread.vo.book.BookSummaryVO;
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
    private final BookChapterRepository chapterRepository;

    @Override
    public BookSummaryVO convertToSummaryVO(BookEntity bookEntity) {
        BookSummaryVO vo = new BookSummaryVO();
        vo.setBookId(bookEntity.getBookId());
        vo.setTitle(bookEntity.getTitle());
        vo.setCover(bookEntity.getCover());
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
        BookCategoryEntity category = categoryRepository.findById(dto.getCategoryId())
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
            BookCategoryEntity category = categoryRepository.findById(dto.getCategoryId())
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
        BookEntity book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("书籍不存在"));
        return convertToDetailVO(book);
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
                    pageable
            );
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

    /**
     * 转换为详情VO
     */
    private BookDetailVO convertToDetailVO(BookEntity book) {
        BookDetailVO vo = new BookDetailVO();
        vo.setBookId(book.getBookId());
        vo.setTitle(book.getTitle());
        vo.setAuthorId(book.getAuthorId());
        if (book.getAuthor() != null) {
            vo.setAuthorName(book.getAuthor().getAuthorName());
        }
        vo.setCover(book.getCover());
        vo.setDescription(book.getDescription());
        vo.setCategoryId(book.getCategoryId());
        
        // 获取分类名称
        Optional<BookCategoryEntity> category = categoryRepository.findById(book.getCategoryId());
        category.ifPresent(c -> vo.setCategoryName(c.getName()));
        
        vo.setPublisher(book.getPublisher());
        vo.setPublishDate(book.getPublishDate());
        vo.setIsbn(book.getIsbn());
        vo.setWordCount(book.getWordCount());
        vo.setPrice(book.getPrice());
        vo.setIsFree(book.getIsFree());
        vo.setIsPublished(book.getIsPublished());
        vo.setIsMemberOnly(book.getIsMemberOnly());
        vo.setTags(book.getTags());
        vo.setRating(book.getRating());
        vo.setReadCount(book.getReadCount());
        vo.setCreatedAt(book.getCreatedAt());
        vo.setUpdatedAt(book.getUpdatedAt());
        
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
        vo.setCover(book.getCover());
        vo.setDescription(book.getDescription());
        vo.setCategoryId(book.getCategoryId());
        
        // 获取分类名称
        Optional<BookCategoryEntity> category = categoryRepository.findById(book.getCategoryId());
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
