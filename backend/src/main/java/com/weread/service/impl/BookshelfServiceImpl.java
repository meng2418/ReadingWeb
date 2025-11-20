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

@Service
@RequiredArgsConstructor
public class BookshelfServiceImpl implements BookshelfService {

    private final BookshelfRepository bookshelfRepository;
    private final ReadingProgressRepository progressRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Override
    @Transactional
    public BookAddVO addBookToShelf(BookAddDTO dto, Integer userId) {
        // 1. 校验书籍存在性
        BookEntity book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("书籍不存在"));

        // 2. 校验是否已在书架
        if (bookshelfRepository.findByUserIdAndBookId(userId, dto.getBookId()).isPresent()) {
            throw new RuntimeException("书籍已在书架中");
        }

        // 3. 保存书架关联记录
        BookshelfEntity shelfEntity = new BookshelfEntity();
        shelfEntity.setUserId(userId);
        shelfEntity.setBookId(dto.getBookId());
        shelfEntity.setStatus(dto.getStatus());
        bookshelfRepository.save(shelfEntity);

        // 4. 初始化阅读进度记录
        ReadingProgressEntity progressEntity = new ReadingProgressEntity();
        progressEntity.setUserId(userId);
        progressEntity.setBookId(dto.getBookId());
        progressRepository.save(progressEntity);

        // 5. 组装返回结果
        AuthorEntity author = authorRepository.findById(book.getAuthorId())
                .orElseThrow(() -> new RuntimeException("作者信息不存在"));
        BookAddVO vo = new BookAddVO();
        vo.setBookId(book.getBookId());
        vo.setTitle(book.getTitle());
        vo.setAuthor(author.getName());
        vo.setCoverUrl(book.getCover());
        vo.setStatus(dto.getStatus());
        vo.setAddedAt(shelfEntity.getAddedAt());
        vo.setMessage("书籍已成功添加到书架");
        return vo;
    }

    @Override
    @Transactional
    public String removeBookFromShelf(Integer bookId, Integer userId) {
        // 1. 校验书籍是否在书架中
        BookshelfEntity shelfEntity = bookshelfRepository.findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new RuntimeException("书籍不在书架中，无法移除"));

        // 2. 删除书架关联记录
        bookshelfRepository.delete(shelfEntity);

        // 3. 删除关联的阅读进度记录
        progressRepository.findByUserIdAndBookId(userId, bookId)
                .ifPresent(progressRepository::delete);

        return "书籍已从书架移除，阅读进度已同步清除";
    }

    @Override
    @Transactional
    public BookStatusVO updateBookStatus(BookStatusUpdateDTO dto, Integer userId) {
        // 1. 校验书籍在书架中
        BookshelfEntity shelfEntity = bookshelfRepository.findByUserIdAndBookId(userId, dto.getBookId())
                .orElseThrow(() -> new RuntimeException("书籍不在书架中"));

        // 2. 校验状态合法性
        if (!List.of("reading", "unread", "finished").contains(dto.getStatus())) {
            throw new RuntimeException("状态必须为 reading/unread/finished");
        }

        // 3. 更新书架中的状态，并同步最后阅读时间
        LocalDateTime now = LocalDateTime.now();
        bookshelfRepository.updateBookStatus(userId, dto.getBookId(), dto.getStatus(), now);

        // 4. 同步更新进度表的最后阅读时间
        progressRepository.findByUserIdAndBookId(userId, dto.getBookId())
                .ifPresent(progress -> {
                    progress.setLastReadAt(now);
                    progressRepository.save(progress);
                });

        // 5. 组装返回结果
        BookStatusVO vo = new BookStatusVO();
        vo.setBookId(dto.getBookId());
        vo.setStatus(dto.getStatus());
        vo.setMessage("阅读状态已更新");
        return vo;
    }

    @Override
    @Transactional
    public ReadingProgressVO updateReadingProgress(ReadingProgressDTO dto, Integer userId) {
        // 1. 校验书籍在书架中
        if (bookshelfRepository.findByUserIdAndBookId(userId, dto.getBookId()).isEmpty()) {
            throw new RuntimeException("书籍不在书架中，无法更新进度");
        }

        // 2. 校验参数合法性
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

        // 5. 组装返回结果
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
    public List<BookShelfVO> getUserBooks(BookshelfQueryDTO dto, Integer userId) {
        // 1. 按条件查询书架记录（支持状态筛选）
        List<BookshelfEntity> shelfEntities;
        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            // 按状态筛选（如 "unread"、"reading"、"finished"）
            shelfEntities = bookshelfRepository.findByUserIdAndStatus(userId, dto.getStatus());
        } else {
            // 查询用户所有书架书籍（全部书架）
            shelfEntities = bookshelfRepository.findByUserId(userId);
        }

        // 2. 关联查询书籍、作者、阅读进度，转换为VO
        return shelfEntities.stream().map(shelf -> {
            // 获取书籍信息
            BookEntity book = bookRepository.findById(shelf.getBookId())
                    .orElseThrow(() -> new RuntimeException("书籍信息不存在：" + shelf.getBookId()));

            // 获取作者信息
            AuthorEntity author = authorRepository.findById(book.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("作者信息不存在：" + book.getAuthorId()));

            // 获取阅读进度（无进度则用默认值）
            ReadingProgressEntity progress = progressRepository
                    .findByUserIdAndBookId(userId, shelf.getBookId())
                    .orElse(new ReadingProgressEntity());

            // 组装VO
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