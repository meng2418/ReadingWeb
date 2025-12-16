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
    public BookAddVO addBookToShelf(BookAddDTO dto, Long userId) {
        // 1. 校验图书是否存在 (bookId 是 Integer)
        Integer bookId = dto.getBookId();
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("图书不存在"));

        // 2. 校验是否已加入书架
        if (bookshelfRepository.findByUserIdAndBookId(userId, bookId).isPresent()) {
            throw new RuntimeException("图书已在书架中");
        }

        // 3. 保存书架记录
        BookshelfEntity shelfEntity = new BookshelfEntity();
        shelfEntity.setUserId(userId);      // userId 是 Long
        shelfEntity.setBookId(bookId);      // bookId 是 Integer
        shelfEntity.setStatus(dto.getStatus());
        bookshelfRepository.save(shelfEntity);

        // 4. 初始化阅读进度
        ReadingProgressEntity progressEntity = new ReadingProgressEntity();
        progressEntity.setUserId(userId);
        progressEntity.setBookId(bookId);
        progressRepository.save(progressEntity);

        // 5. 查作者
        Long authorIdLong = book.getAuthorId();
        if (authorIdLong == null) {
            throw new RuntimeException("图书作者信息缺失");
        }
        

        AuthorEntity author = authorRepository.findById(authorIdLong)
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
        // 1. У���鼮�Ƿ��������
        BookshelfEntity shelfEntity = bookshelfRepository.findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new RuntimeException("�鼮��������У��޷��Ƴ�"));

        // 2. ɾ����ܹ�����¼
        bookshelfRepository.delete(shelfEntity);

        // 3. ɾ���������Ķ����ȼ�¼
        progressRepository.findByUserIdAndBookId(userId, bookId)
                .ifPresent(progressRepository::delete);

        return "�鼮�Ѵ�����Ƴ����Ķ�������ͬ�����";
    }

    @Override
    @Transactional
    public BookStatusVO updateBookStatus(BookStatusUpdateDTO dto, Long userId) {
        // 1. У���鼮�������
        BookshelfEntity shelfEntity = bookshelfRepository.findByUserIdAndBookId(userId, dto.getBookId())
                .orElseThrow(() -> new RuntimeException("�鼮���������"));

        // 2. У��״̬�Ϸ���
        if (!List.of("reading", "unread", "finished").contains(dto.getStatus())) {
            throw new RuntimeException("״̬����Ϊ reading/unread/finished");
        }

        // 3. ��������е�״̬����ͬ������Ķ�ʱ��
        LocalDateTime now = LocalDateTime.now();
        bookshelfRepository.updateBookStatus(userId, dto.getBookId(), dto.getStatus(), now);

        // 4. ͬ�����½��ȱ�������Ķ�ʱ��
        progressRepository.findByUserIdAndBookId(userId, dto.getBookId())
                .ifPresent(progress -> {
                    progress.setLastReadAt(now);
                    progressRepository.save(progress);
                });

        // 5. ��װ���ؽ��
        BookStatusVO vo = new BookStatusVO();
        vo.setBookId(dto.getBookId());
        vo.setStatus(dto.getStatus());
        vo.setMessage("�Ķ�״̬�Ѹ���");
        return vo;
    }

    @Override
    @Transactional
    public ReadingProgressVO updateReadingProgress(ReadingProgressDTO dto, Long userId) {
        // 1. У���鼮�������
        if (bookshelfRepository.findByUserIdAndBookId(userId, dto.getBookId()).isEmpty()) {
            throw new RuntimeException("�鼮��������У��޷����½���");
        }

        // 2. У������Ϸ���
        if (dto.getProgress() == null || dto.getProgress() < 0 || dto.getProgress() > 1) {
            throw new RuntimeException("����ֵ������ 0-1 ֮��");
        }
        if (dto.getCurrentPage() == null || dto.getCurrentPage() < 1) {
            throw new RuntimeException("ҳ�����Ϊ������");
        }

        // 3. ���½��ȱ�
        LocalDateTime now = LocalDateTime.now();
        progressRepository.updateProgress(
                userId,
                dto.getBookId(),
                dto.getChapterId(),
                dto.getCurrentPage(),
                dto.getProgress(),
                now);

        // 4. ͬ��������ܱ�������Ķ�ʱ��
        bookshelfRepository.findByUserIdAndBookId(userId, dto.getBookId())
                .ifPresent(shelf -> {
                    shelf.setLastReadAt(now);
                    bookshelfRepository.save(shelf);
                });

        // 5. ��װ���ؽ��
        ReadingProgressVO vo = new ReadingProgressVO();
        vo.setBookId(dto.getBookId());
        vo.setChapterId(dto.getChapterId());
        vo.setCurrentPage(dto.getCurrentPage());
        vo.setProgress(dto.getProgress());
        vo.setLastReadTime(now);
        vo.setMessage("�Ķ������Ѹ���");
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

            // === 获取图书 ===
            Integer bookId = shelf.getBookId();
            BookEntity book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("图书不存在: " + bookId));

            // === 获取作者 (注意类型 Integer -> Long 转换) ===
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