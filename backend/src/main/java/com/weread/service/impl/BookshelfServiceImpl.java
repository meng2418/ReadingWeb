package com.weread.service.impl;

import com.weread.dto.bookshelf.*;
import com.weread.entity.AuthorEntity;
import com.weread.entity.BookEntity;
import com.weread.entity.BookshelfBookEntity;
import com.weread.repository.AuthorRepository;
import com.weread.repository.BookRepository;
import com.weread.repository.BookshelfBookRepository;
import com.weread.service.BookshelfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookshelfServiceImpl implements BookshelfService {

    // 注入所有需要的Repository
    private final BookshelfBookRepository bookshelfBookRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Override
    @Transactional
    public BookAddVO addBookToShelf(BookAddDTO dto, Integer userId) {
        // 1. 校验书籍是否存在
        BookEntity book = bookRepository.findByBookId(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("书籍不存在"));

        // 2. 通过书籍的authorId查询作者信息（获取作者姓名）
        AuthorEntity author = authorRepository.findById(book.getAuthorId())
                .orElseThrow(() -> new RuntimeException("作者信息不存在"));

        // 3. 校验书籍是否已在书架中（避免重复添加）
        if (bookshelfBookRepository.findByUserIdAndBookId(userId, dto.getBookId()).isPresent()) {
            throw new RuntimeException("书籍已在书架中");
        }

        // 4. 校验阅读状态是否合法
        if (!List.of("reading", "unread", "finished").contains(dto.getStatus())) {
            throw new RuntimeException("状态必须为 reading/unread/finished");
        }

        // 5. 创建书架-书籍关联记录并保存
        BookshelfBookEntity shelfBook = new BookshelfBookEntity();
        shelfBook.setUserId(userId);
        shelfBook.setBookId(dto.getBookId());
        shelfBook.setStatus(dto.getStatus());
        shelfBook.setAddTime(LocalDateTime.now()); // 记录添加时间
        bookshelfBookRepository.save(shelfBook);

        // 6. 组装返回VO（填充作者姓名到author字段）
        BookAddVO vo = new BookAddVO();
        vo.setBookId(book.getBookId());
        vo.setTitle(book.getTitle());
        vo.setAuthor(author.getName()); // 作者姓名（来自AuthorEntity的name字段）
        vo.setCoverUrl(book.getCover());
        vo.setStatus(dto.getStatus());
        vo.setAddedAt(shelfBook.getAddTime());
        vo.setMessage("书籍已成功添加到书架");
        return vo;
    }

    @Override
    @Transactional
    public String removeBookFromShelf(Integer bookId, Integer userId) {
        // 1. 校验书籍是否在书架中
        BookshelfBookEntity shelfBook = bookshelfBookRepository.findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new RuntimeException("书籍不在书架中"));

        // 2. 从书架中删除关联记录
        bookshelfBookRepository.delete(shelfBook);
        return "书籍已从书架移除";
    }

    @Override
    @Transactional
    public BookStatusVO updateBookStatus(BookStatusUpdateDTO dto, Integer userId) {
        // 1. 校验书籍是否在书架中
        BookshelfBookEntity shelfBook = bookshelfBookRepository.findByUserIdAndBookId(userId, dto.getBookId())
                .orElseThrow(() -> new RuntimeException("书籍不在书架中"));

        // 2. 校验状态是否合法
        if (!List.of("reading", "unread", "finished").contains(dto.getStatus())) {
            throw new RuntimeException("状态必须为 reading/unread/finished");
        }

        // 3. 更新书籍阅读状态
        shelfBook.setStatus(dto.getStatus());
        bookshelfBookRepository.save(shelfBook);

        // 4. 组装返回VO
        BookStatusVO vo = new BookStatusVO();
        vo.setBookId(dto.getBookId());
        vo.setStatus(dto.getStatus());
        vo.setMessage("阅读状态已更新");
        return vo;
    }

    @Override
    @Transactional
    public ReadingProgressVO updateReadingProgress(ReadingProgressDTO dto, Integer userId) {
        // 1. 校验书籍是否在书架中
        if (bookshelfBookRepository.findByUserIdAndBookId(userId, dto.getBookId()).isEmpty()) {
            throw new RuntimeException("书籍不在书架中，无法更新进度");
        }

        // 2. 调用Repository更新阅读进度（含最后阅读时间）
        LocalDateTime now = LocalDateTime.now();
        bookshelfBookRepository.updateReadingProgress(
                userId,
                dto.getBookId(),
                dto.getChapterIndex(),
                dto.getPageNum(),
                now);

        // 3. 组装返回VO
        ReadingProgressVO vo = new ReadingProgressVO();
        vo.setBookId(dto.getBookId());
        vo.setChapterIndex(dto.getChapterIndex());
        vo.setPageNum(dto.getPageNum());
        vo.setLastReadTime(now);
        vo.setMessage("阅读进度已更新");
        return vo;
    }

    @Override
    public List<BookShelfVO> getUserBooks(BookshelfQueryDTO dto, Integer userId) {
        // 1. 按条件查询书架中的所有书籍（无分页）
        List<BookshelfBookEntity> shelfBooks;
        if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
            // 按状态筛选所有符合条件的书籍
            shelfBooks = bookshelfBookRepository.findByUserIdAndStatus(userId, dto.getStatus());
        } else {
            // 查询用户书架中的所有书籍
            shelfBooks = bookshelfBookRepository.findByUserId(userId);
        }

        // 2. 转换为VO（关联书籍和作者信息）
        return shelfBooks.stream().map(shelfBook -> {
            // 获取书籍基本信息
            BookEntity book = bookRepository.findByBookId(shelfBook.getBookId())
                    .orElseThrow(() -> new RuntimeException("书籍信息不存在"));
            // 获取作者姓名
            AuthorEntity author = authorRepository.findById(book.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("作者信息不存在"));

            // 组装VO
            BookShelfVO vo = new BookShelfVO();
            vo.setBookId(book.getBookId());
            vo.setTitle(book.getTitle());
            vo.setAuthor(author.getName()); // 作者姓名
            vo.setCoverUrl(book.getCover());
            vo.setStatus(shelfBook.getStatus());
            vo.setChapterIndex(shelfBook.getChapterIndex());
            vo.setLastReadTime(shelfBook.getLastReadTime());
            return vo;
        }).collect(Collectors.toList());
    }
}