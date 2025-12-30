package com.weread.service.impl;

import com.weread.dto.bookshelf.*;
import com.weread.entity.*;
import com.weread.repository.*;
import com.weread.service.BookshelfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 书架服务实现类
 * 提供图书添加到书架、移除、状态更新、阅读进度更新等功能
 */
@Service
@RequiredArgsConstructor
public class BookshelfServiceImpl implements BookshelfService {

    private final BookshelfRepository bookshelfRepository;
    private final ReadingProgressRepository progressRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Override
    @Transactional
    public BookAddVO addBookToShelf(BookAddDTO dto, Long userId) {
        // 1. 校验是否已加入书架（先检查书架，避免不必要的图书查询）
        Integer bookId = dto.getBookId();
        if (bookshelfRepository.findByUserIdAndBookId(userId, bookId).isPresent()) {
            throw new RuntimeException("图书已在书架中");
        }

        // 2. 校验图书是否存在
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("图书不存在"));

        // 3. 保存书架记录
        BookshelfEntity shelfEntity = new BookshelfEntity();
        shelfEntity.setUserId(userId);
        shelfEntity.setBookId(bookId);
        shelfEntity.setStatus(dto.getStatus());
        bookshelfRepository.save(shelfEntity);

        // 4. 初始化阅读进度
        ReadingProgressEntity progressEntity = new ReadingProgressEntity();
        progressEntity.setUserId(userId);
        progressEntity.setBookId(bookId);
        progressRepository.save(progressEntity);

        // 5. 查询作者信息
        Long authorId = book.getAuthorId();
        if (authorId == null) {
            throw new RuntimeException("图书作者信息缺失");
        }

        AuthorEntity author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("作者信息不存在"));

        // 6. 封装返回值
        BookAddVO vo = new BookAddVO();
        vo.setBookId(book.getBookId());
        vo.setTitle(book.getTitle());
        vo.setAuthor(author.getName());
        vo.setCoverUrl(book.getCover());
        vo.setStatus(dto.getStatus());
        vo.setAddedAt(shelfEntity.getAddedAt());
        vo.setMessage("图书已成功添加到书架");
        return vo;
    }

    @Override
    @Transactional
    public String removeBookFromShelf(Integer bookId, Long userId) {
        // 1. 校验图书是否在书架中
        BookshelfEntity shelfEntity = bookshelfRepository.findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new RuntimeException("图书不在书架中，无法移除"));

        // 2. 删除书架记录
        bookshelfRepository.delete(shelfEntity);

        // 3. 删除对应的阅读进度记录
        progressRepository.findByUserIdAndBookId(userId, bookId)
                .ifPresent(progressRepository::delete);

        return "图书已成功从书架移除，阅读进度已同步删除";
    }

    @Override
    @Transactional
    public BookStatusVO updateBookStatus(BookStatusUpdateDTO dto, Long userId) {
        // 1. 校验图书是否在书架中
        bookshelfRepository.findByUserIdAndBookId(userId, dto.getBookId())
                .orElseThrow(() -> new RuntimeException("图书不在书架中"));

        // 2. 校验状态合法性
        if (!List.of("reading", "unread", "finished").contains(dto.getStatus())) {
            throw new RuntimeException("状态必须为 reading/unread/finished");
        }

        // 3. 更新书架中的状态，同时更新最后阅读时间
        LocalDateTime now = LocalDateTime.now();
        bookshelfRepository.updateBookStatus(userId, dto.getBookId(), dto.getStatus(), now);

        // 4. 同步更新进度表的最后阅读时间
        progressRepository.findByUserIdAndBookId(userId, dto.getBookId())
                .ifPresent(progress -> {
                    progress.setLastReadAt(now);
                    progressRepository.save(progress);
                });

        // 5. 封装返回结果
        BookStatusVO vo = new BookStatusVO();
        vo.setBookId(dto.getBookId());
        vo.setStatus(dto.getStatus());
        vo.setMessage("阅读状态已更新");
        return vo;
    }

    @Override
    @Transactional
    public ReadingProgressVO updateReadingProgress(ReadingProgressDTO dto, Long userId) {
        // 1. 校验图书是否在书架中
        if (bookshelfRepository.findByUserIdAndBookId(userId, dto.getBookId()).isEmpty()) {
            throw new RuntimeException("图书不在书架中，无法更新进度");
        }

        // 2. 校验进度合法性
        if (dto.getProgress() == null || dto.getProgress() < 0 || dto.getProgress() > 1) {
            throw new RuntimeException("进度值必须在 0-1 之间");
        }
        if (dto.getCurrentPage() == null || dto.getCurrentPage() < 1) {
            throw new RuntimeException("页码必须为正整数");
        }

        // 3. 更新进度表
        LocalDateTime now = LocalDateTime.now();
        progressRepository.updateProgress(
                userId,
                dto.getBookId(),
                dto.getChapterId(),
                dto.getCurrentPage(),
                dto.getProgress(),
                now);

        // 4. 同步更新书架表的最后阅读时间
        bookshelfRepository.findByUserIdAndBookId(userId, dto.getBookId())
                .ifPresent(shelf -> {
                    shelf.setLastReadAt(now);
                    bookshelfRepository.save(shelf);
                });

        // 5. 封装返回结果
        ReadingProgressVO vo = new ReadingProgressVO();
        vo.setBookId(dto.getBookId());
        vo.setChapterId(dto.getChapterId());
        vo.setCurrentPage(dto.getCurrentPage());
        vo.setProgress(dto.getProgress());
        vo.setLastReadTime(now);
        vo.setMessage("阅读进度已更新");
        return vo;
    }

    @Override
    public List<BookShelfVO> getUserBooks(BookshelfQueryDTO dto, Long userId) {
        // 1. 查询书架记录，可按状态过滤
        List<BookshelfEntity> shelfEntities;

        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            shelfEntities = bookshelfRepository.findByUserIdAndStatus(userId, dto.getStatus());
        } else {
            shelfEntities = bookshelfRepository.findByUserId(userId);
        }

        // 2. 转换为VO
        return shelfEntities.stream().map(shelf -> {
            // === 获取图书信息 ===
            Integer bookId = shelf.getBookId();
            BookEntity book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("图书不存在: " + bookId));

            // === 获取作者信息 ===
            Long authorId = book.getAuthorId();
            if (authorId == null) {
                throw new RuntimeException("图书缺少作者字段：" + bookId);
            }

            AuthorEntity author = authorRepository.findById(authorId)
                    .orElseThrow(() -> new RuntimeException("作者不存在：" + authorId));

            // === 获取阅读进度 ===
            ReadingProgressEntity progress = progressRepository
                    .findByUserIdAndBookId(userId, bookId)
                    .orElse(new ReadingProgressEntity());

            // === 封装VO ===
            BookShelfVO vo = new BookShelfVO();
            vo.setBookId(book.getBookId());
            vo.setTitle(book.getTitle());
            vo.setAuthor(author.getName());
            vo.setCoverUrl(book.getCover());
            vo.setStatus(shelf.getStatus());
            vo.setChapterId(progress.getChapterId());
            vo.setCurrentPage(progress.getCurrentPage());
            vo.setProgress(progress.getProgress());
            vo.setAddedAt(shelf.getAddedAt());
            vo.setLastReadTime(shelf.getLastReadAt());

            return vo;
        }).collect(Collectors.toList());
    }
}
