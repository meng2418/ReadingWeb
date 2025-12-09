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
        // 1. У���鼮������
        BookEntity book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("�鼮������"));

        // 2. У���Ƿ��������
        if (bookshelfRepository.findByUserIdAndBookId(userId, dto.getBookId()).isPresent()) {
            throw new RuntimeException("�鼮���������");
        }

        // 3. ������ܹ�����¼
        BookshelfEntity shelfEntity = new BookshelfEntity();
        shelfEntity.setUserId(userId);
        shelfEntity.setBookId(dto.getBookId());
        shelfEntity.setStatus(dto.getStatus());
        bookshelfRepository.save(shelfEntity);

        // 4. ��ʼ���Ķ����ȼ�¼
        ReadingProgressEntity progressEntity = new ReadingProgressEntity();
        progressEntity.setUserId(userId);
        progressEntity.setBookId(dto.getBookId());
        progressRepository.save(progressEntity);

        // 5. ��װ���ؽ��
        AuthorEntity author = authorRepository.findById(book.getAuthorId())
                .orElseThrow(() -> new RuntimeException("������Ϣ������"));
        BookAddVO vo = new BookAddVO();
        vo.setBookId(book.getBookId());
        vo.setTitle(book.getTitle());
        vo.setAuthor(author.getName());
        vo.setCoverUrl(book.getCover());
        vo.setStatus(dto.getStatus());
        vo.setAddedAt(shelfEntity.getAddedAt());
        vo.setMessage("�鼮�ѳɹ����ӵ����");
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
        // 1. ��������ѯ��ܼ�¼��֧��״̬ɸѡ��
        List<BookshelfEntity> shelfEntities;
        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            // ��״̬ɸѡ���� "unread"��"reading"��"finished"��
            shelfEntities = bookshelfRepository.findByUserIdAndStatus(userId, dto.getStatus());
        } else {
            // ��ѯ�û���������鼮��ȫ����ܣ�
            shelfEntities = bookshelfRepository.findByUserId(userId);
        }

        // 2. ������ѯ�鼮�����ߡ��Ķ����ȣ�ת��ΪVO
        return shelfEntities.stream().map(shelf -> {
            // ��ȡ�鼮��Ϣ
            BookEntity book = bookRepository.findById(shelf.getBookId())
                    .orElseThrow(() -> new RuntimeException("�鼮��Ϣ�����ڣ�" + shelf.getBookId()));

            // ��ȡ������Ϣ
            AuthorEntity author = authorRepository.findById(book.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("������Ϣ�����ڣ�" + book.getAuthorId()));

            // ��ȡ�Ķ����ȣ��޽�������Ĭ��ֵ��
            ReadingProgressEntity progress = progressRepository
                    .findByUserIdAndBookId(userId, shelf.getBookId())
                    .orElse(new ReadingProgressEntity());

            // ��װVO
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