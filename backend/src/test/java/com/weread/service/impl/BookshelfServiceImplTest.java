package com.weread.service.impl;

import com.weread.dto.bookshelf.*;
import com.weread.entity.*;
import com.weread.repository.AuthorRepository;
import com.weread.repository.BookRepository;
import com.weread.repository.BookshelfRepository;
import com.weread.repository.ReadingProgressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookshelfServiceImplTest {

    @Mock
    private BookshelfRepository bookshelfRepository;

    @Mock
    private ReadingProgressRepository progressRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookshelfServiceImpl bookshelfService;

    // ��������
    private final Integer userId = 1;
    private final Integer bookId = 1001;
    private final Integer authorId = 2001;

    // ====================== ���� addBookToShelf ���� ======================
    @Test
    public void testAddBookToShelf_Success() {
        // 1. ׼����������
        BookAddDTO dto = new BookAddDTO();
        dto.setBookId(bookId);
        dto.setStatus("reading");

        // 2. Mock ��������
        BookEntity mockBook = new BookEntity();
        mockBook.setBookId(bookId);
        mockBook.setTitle("�����鼮");
        mockBook.setAuthorId(authorId);
        mockBook.setCover("cover.jpg");

        AuthorEntity mockAuthor = new AuthorEntity();
        mockAuthor.setAuthorId(authorId);
        mockAuthor.setAuthorName("��������");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));
        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.empty()); // �鼮�������
        when(authorRepository.findByAuthorId(authorId)).thenReturn(Optional.of(mockAuthor));
        when(bookshelfRepository.save(any(BookshelfEntity.class))).thenAnswer(invocation -> {
            BookshelfEntity entity = invocation.getArgument(0);
            entity.setBookshelfId(1); // ģ������ID
            entity.setAddedAt(LocalDateTime.now());
            return entity;
        });
        when(progressRepository.save(any(ReadingProgressEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // 3. ִ�з���
        BookAddVO result = bookshelfService.addBookToShelf(dto, userId);

        // 4. ��֤���
        assertNotNull(result);
        assertEquals(bookId, result.getBookId());
        assertEquals("�����鼮", result.getTitle());
        assertEquals("��������", result.getAuthor());
        assertEquals("cover.jpg", result.getCoverUrl());
        assertEquals("reading", result.getStatus());
        assertNotNull(result.getAddedAt());
        assertEquals("图书已成功添加到书架", result.getMessage());

        // 5. ��֤��������
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookshelfRepository, times(1)).findByUserIdAndBookId(userId, bookId);
        verify(bookshelfRepository, times(1)).save(any());
        verify(progressRepository, times(1)).save(any());
        verify(authorRepository, times(1)).findByAuthorId(authorId);
    }

    @Test
    public void testAddBookToShelf_BookAlreadyExists() {
        // 1. ׼������
        BookAddDTO dto = new BookAddDTO();
        dto.setBookId(bookId);

        // 2. Mock���鼮�������
        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(new BookshelfEntity()));

        // 3. ִ�в���֤�쳣
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookshelfService.addBookToShelf(dto, userId);
        });
        assertEquals("图书已在书架中", exception.getMessage());

        // 4. ��֤δ���ñ��淽��
        verify(bookshelfRepository, never()).save(any());
        verify(progressRepository, never()).save(any());
    }

    @Test
    public void testAddBookToShelf_BookNotFound() {
        // 1. ׼������
        BookAddDTO dto = new BookAddDTO();
        dto.setBookId(bookId);

        // 2. Mock���鼮������
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // 3. ִ�в���֤�쳣
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookshelfService.addBookToShelf(dto, userId);
        });
        assertEquals("图书不存在", exception.getMessage());
    }

    // ====================== ���� removeBookFromShelf ���� ======================
    @Test
    public void testRemoveBookFromShelf_Success() {
        // 1. Mock ����
        BookshelfEntity mockShelf = new BookshelfEntity();
        mockShelf.setBookshelfId(1);
        mockShelf.setUserId(userId);
        mockShelf.setBookId(bookId);

        ReadingProgressEntity mockProgress = new ReadingProgressEntity();
        mockProgress.setReadingProgressId(1);

        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(mockShelf));
        when(progressRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(mockProgress));

        // 2. ִ�з���
        String result = bookshelfService.removeBookFromShelf(bookId, userId);

        // 3. ��֤���
        assertEquals("图书已成功从书架移除，阅读进度已同步删除", result);

        // 4. ��֤ɾ������
        verify(bookshelfRepository, times(1)).delete(mockShelf);
        verify(progressRepository, times(1)).delete(mockProgress);
    }

    @Test
    public void testRemoveBookFromShelf_BookNotInShelf() {
        // 1. Mock���鼮�������
        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.empty());

        // 2. ִ�в���֤�쳣
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookshelfService.removeBookFromShelf(bookId, userId);
        });
        assertEquals("图书不在书架中，无法移除", exception.getMessage());

        // 3. ��֤δִ��ɾ��
        verify(bookshelfRepository, never()).delete(any());
        verify(progressRepository, never()).delete(any());
    }

    // ====================== ���� updateBookStatus ���� ======================
    @Test
    public void testUpdateBookStatus_Success() {
        // 1. ׼������
        BookStatusUpdateDTO dto = new BookStatusUpdateDTO();
        dto.setBookId(bookId);
        dto.setStatus("finished");

        // 2. Mock ����
        BookshelfEntity mockShelf = new BookshelfEntity();
        mockShelf.setUserId(userId);
        mockShelf.setBookId(bookId);

        ReadingProgressEntity mockProgress = new ReadingProgressEntity();
        mockProgress.setReadingProgressId(1);

        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(mockShelf));
        when(progressRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(mockProgress));

        // 3. ִ�з���
        BookStatusVO result = bookshelfService.updateBookStatus(dto, userId);

        // 4. ��֤���
        assertNotNull(result);
        assertEquals(bookId, result.getBookId());
        assertEquals("finished", result.getStatus());
        assertEquals("阅读状态已更新", result.getMessage());

        // 5. ��֤���²���
        verify(bookshelfRepository, times(1)).updateBookStatus(eq(userId), eq(bookId), eq("finished"),
                any(LocalDateTime.class));
        verify(progressRepository, times(1)).save(mockProgress); // ��֤���ȱ�ʱ�����
    }

    @Test
    public void testUpdateBookStatus_InvalidStatus() {
        // 1. ׼�����ݣ��Ƿ�״̬��
        BookStatusUpdateDTO dto = new BookStatusUpdateDTO();
        dto.setBookId(bookId);
        dto.setStatus("invalid");

        // 2. Mock���鼮�����
        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(new BookshelfEntity()));

        // 3. ִ�в���֤�쳣
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookshelfService.updateBookStatus(dto, userId);
        });
        assertEquals("状态必须为 reading/unread/finished", exception.getMessage());
    }

    @Test
    public void testUpdateBookStatus_BookNotInShelf() {
        // 1. ׼������
        BookStatusUpdateDTO dto = new BookStatusUpdateDTO();
        dto.setBookId(bookId);

        // 2. Mock���鼮�������
        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.empty());

        // 3. ִ�в���֤�쳣
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookshelfService.updateBookStatus(dto, userId);
        });
        assertEquals("图书不在书架中", exception.getMessage());
    }

    // ====================== ���� updateReadingProgress ���� ======================
    @Test
    public void testUpdateReadingProgress_Success() {
        // 1. ׼������
        ReadingProgressDTO dto = new ReadingProgressDTO();
        dto.setBookId(bookId);
        dto.setChapterId(5);
        dto.setCurrentPage(120);
        dto.setProgress(0.6f);

        // 2. Mock ����
        BookshelfEntity mockShelf = new BookshelfEntity();
        mockShelf.setBookshelfId(1);

        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(mockShelf));

        // 3. ִ�з���
        ReadingProgressVO result = bookshelfService.updateReadingProgress(dto, userId);

        // 4. ��֤���
        assertNotNull(result);
        assertEquals(bookId, result.getBookId());
        assertEquals(5, result.getChapterId());
        assertEquals(120, result.getCurrentPage());
        assertEquals(0.6f, result.getProgress());
        assertNotNull(result.getLastReadTime());
        assertEquals("阅读进度已更新", result.getMessage());

        // 5. ��֤���²���
        verify(progressRepository, times(1)).updateProgress(
                eq(userId), eq(bookId), eq(5), eq(120), eq(0.6f), any(LocalDateTime.class));
        verify(bookshelfRepository, times(1)).save(mockShelf); // ��֤��ܱ�ʱ�����
    }

    @Test
    public void testUpdateReadingProgress_InvalidProgress() {
        // 1. ׼�����ݣ����ȳ�����Χ��
        ReadingProgressDTO dto = new ReadingProgressDTO();
        dto.setBookId(bookId);
        dto.setProgress(1.2f); // �Ƿ�ֵ

        // 2. Mock���鼮�����
        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(new BookshelfEntity()));

        // 3. ִ�в���֤�쳣
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookshelfService.updateReadingProgress(dto, userId);
        });
        assertEquals("进度值必须在 0-1 之间", exception.getMessage());
    }

    @Test
    public void testUpdateReadingProgress_InvalidPage() {
        // 1. ׼�����ݣ�ҳ��Ƿ���
        ReadingProgressDTO dto = new ReadingProgressDTO();
        dto.setBookId(bookId);
        dto.setCurrentPage(0); // �Ƿ�ֵ
        dto.setProgress(0.5f);

        // 2. Mock���鼮�����
        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(new BookshelfEntity()));

        // 3. ִ�в���֤�쳣
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookshelfService.updateReadingProgress(dto, userId);
        });
        assertEquals("页码必须为正整数", exception.getMessage());
    }

    @Test
    public void testUpdateReadingProgress_BookNotInShelf() {
        // 1. ׼������
        ReadingProgressDTO dto = new ReadingProgressDTO();
        dto.setBookId(bookId);

        // 2. Mock���鼮�������
        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.empty());

        // 3. ִ�в���֤�쳣
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookshelfService.updateReadingProgress(dto, userId);
        });
        assertEquals("图书不在书架中，无法更新进度", exception.getMessage());
    }

    // ====================== ���� getUserBooks ���� ======================
    @Test
    public void testGetUserBooks_WithStatusFilter() {
        // 1. ׼����ѯ����
        BookshelfQueryDTO dto = new BookshelfQueryDTO();
        dto.setStatus("reading");

        // 2. Mock ��������
        BookshelfEntity shelf1 = new BookshelfEntity();
        shelf1.setBookshelfId(1);
        shelf1.setUserId(userId);
        shelf1.setBookId(bookId);
        shelf1.setStatus("reading");
        shelf1.setAddedAt(LocalDateTime.now());

        BookEntity book = new BookEntity();
        book.setBookId(bookId);
        book.setTitle("�����鼮");
        book.setAuthorId(authorId);
        book.setCover("cover.jpg");

        AuthorEntity author = new AuthorEntity();
        author.setAuthorId(authorId);
        author.setAuthorName("��������");

        ReadingProgressEntity progress = new ReadingProgressEntity();
        progress.setChapterId(3);
        progress.setCurrentPage(50);
        progress.setProgress(0.3f);

        when(bookshelfRepository.findByUserIdAndStatus(userId, "reading")).thenReturn(List.of(shelf1));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(authorRepository.findByAuthorId(authorId)).thenReturn(Optional.of(author));
        when(progressRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(progress));

        // 3. ִ�з���
        List<BookShelfVO> result = bookshelfService.getUserBooks(dto, userId);

        // 4. ��֤���
        assertNotNull(result);
        assertEquals(1, result.size());

        BookShelfVO vo = result.get(0);
        assertEquals(bookId, vo.getBookId());
        assertEquals("�����鼮", vo.getTitle());
        assertEquals("��������", vo.getAuthor());
        assertEquals("cover.jpg", vo.getCoverUrl());
        assertEquals("reading", vo.getStatus());
        assertEquals(3, vo.getChapterId());
        assertEquals(50, vo.getCurrentPage());
        assertEquals(0.3f, vo.getProgress());
    }

    @Test
    public void testGetUserBooks_WithoutStatusFilter() {
        // 1. ׼����ѯ��������ɸѡ״̬��
        BookshelfQueryDTO dto = new BookshelfQueryDTO();

        // 2. Mock �������ݣ�����2���飩
        BookshelfEntity shelf1 = new BookshelfEntity();
        shelf1.setBookId(bookId);
        shelf1.setStatus("reading");

        BookshelfEntity shelf2 = new BookshelfEntity();
        shelf2.setBookId(1002); // �ڶ�����ID
        shelf2.setStatus("finished");

        // Ϊ������������Ч�� authorId���� stub һ�£�
        BookEntity book1 = new BookEntity();
        book1.setBookId(bookId);
        book1.setAuthorId(authorId); // ʹ�ò������ж���� authorId=2001

        BookEntity book2 = new BookEntity();
        book2.setBookId(1002);
        book2.setAuthorId(authorId); // ͳһʹ��ͬһ�� authorId

        // ���� stub������ѯ authorId=2001 ʱ���� mock ����
        when(bookshelfRepository.findByUserId(userId)).thenReturn(List.of(shelf1, shelf2));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(1002)).thenReturn(Optional.of(book2));
        when(authorRepository.findByAuthorId(authorId)).thenReturn(Optional.of(new AuthorEntity())); // ƥ�� authorId=2001
        when(progressRepository.findByUserIdAndBookId((int) anyLong(), any(Integer.class)))
        .thenReturn(Optional.of(new ReadingProgressEntity()));


        // 3. ִ�з���
        List<BookShelfVO> result = bookshelfService.getUserBooks(dto, userId);

        // 4. ��֤���
        assertEquals(2, result.size()); // ��֤����2������
    }

    @Test
    public void testGetUserBooks_NoBooks() {
        // 1. ׼����ѯ����
        BookshelfQueryDTO dto = new BookshelfQueryDTO();

        // 2. Mock���û�������鼮
        when(bookshelfRepository.findByUserId(userId)).thenReturn(List.of());

        // 3. ִ�з���
        List<BookShelfVO> result = bookshelfService.getUserBooks(dto, userId);

        // 4. ��֤���
        assertTrue(result.isEmpty());
    }
}


