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
import static org.mockito.ArgumentMatchers.anyInt;
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

    // 测试数据
    private final Integer userId = 1;
    private final Integer bookId = 1001;
    private final Integer authorId = 2001;

    // ====================== 测试 addBookToShelf 方法 ======================
    @Test
    public void testAddBookToShelf_Success() {
        // 1. 准备请求数据
        BookAddDTO dto = new BookAddDTO();
        dto.setBookId(bookId);
        dto.setStatus("reading");

        // 2. Mock 依赖返回
        BookEntity mockBook = new BookEntity();
        mockBook.setBookId(bookId);
        mockBook.setTitle("测试书籍");
        mockBook.setAuthorId(authorId);
        mockBook.setCover("cover.jpg");

        AuthorEntity mockAuthor = new AuthorEntity();
        mockAuthor.setAuthorId(authorId);
        mockAuthor.setName("测试作者");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));
        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.empty()); // 书籍不在书架
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(mockAuthor));
        when(bookshelfRepository.save(any(BookshelfEntity.class))).thenAnswer(invocation -> {
            BookshelfEntity entity = invocation.getArgument(0);
            entity.setBookshelfId(1); // 模拟生成ID
            entity.setAddedAt(LocalDateTime.now());
            return entity;
        });
        when(progressRepository.save(any(ReadingProgressEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // 3. 执行方法
        BookAddVO result = bookshelfService.addBookToShelf(dto, userId);

        // 4. 验证结果
        assertNotNull(result);
        assertEquals(bookId, result.getBookId());
        assertEquals("测试书籍", result.getTitle());
        assertEquals("测试作者", result.getAuthor());
        assertEquals("cover.jpg", result.getCoverUrl());
        assertEquals("reading", result.getStatus());
        assertNotNull(result.getAddedAt());
        assertEquals("书籍已成功添加到书架", result.getMessage());

        // 5. 验证依赖调用
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookshelfRepository, times(1)).findByUserIdAndBookId(userId, bookId);
        verify(bookshelfRepository, times(1)).save(any());
        verify(progressRepository, times(1)).save(any());
        verify(authorRepository, times(1)).findById(authorId);
    }

    @Test
    public void testAddBookToShelf_BookAlreadyExists() {
        // 1. 准备数据
        BookAddDTO dto = new BookAddDTO();
        dto.setBookId(bookId);

        // 2. Mock：书籍已在书架
        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(new BookshelfEntity()));

        // 3. 执行并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookshelfService.addBookToShelf(dto, userId);
        });
        assertEquals("书籍已在书架中", exception.getMessage());

        // 4. 验证未调用保存方法
        verify(bookshelfRepository, never()).save(any());
        verify(progressRepository, never()).save(any());
    }

    @Test
    public void testAddBookToShelf_BookNotFound() {
        // 1. 准备数据
        BookAddDTO dto = new BookAddDTO();
        dto.setBookId(bookId);

        // 2. Mock：书籍不存在
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // 3. 执行并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookshelfService.addBookToShelf(dto, userId);
        });
        assertEquals("书籍不存在", exception.getMessage());
    }

    // ====================== 测试 removeBookFromShelf 方法 ======================
    @Test
    public void testRemoveBookFromShelf_Success() {
        // 1. Mock 数据
        BookshelfEntity mockShelf = new BookshelfEntity();
        mockShelf.setBookshelfId(1);
        mockShelf.setUserId(userId);
        mockShelf.setBookId(bookId);

        ReadingProgressEntity mockProgress = new ReadingProgressEntity();
        mockProgress.setReadingProgressId(1);

        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(mockShelf));
        when(progressRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(mockProgress));

        // 2. 执行方法
        String result = bookshelfService.removeBookFromShelf(bookId, userId);

        // 3. 验证结果
        assertEquals("书籍已从书架移除，阅读进度已同步清除", result);

        // 4. 验证删除操作
        verify(bookshelfRepository, times(1)).delete(mockShelf);
        verify(progressRepository, times(1)).delete(mockProgress);
    }

    @Test
    public void testRemoveBookFromShelf_BookNotInShelf() {
        // 1. Mock：书籍不在书架
        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.empty());

        // 2. 执行并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookshelfService.removeBookFromShelf(bookId, userId);
        });
        assertEquals("书籍不在书架中，无法移除", exception.getMessage());

        // 3. 验证未执行删除
        verify(bookshelfRepository, never()).delete(any());
        verify(progressRepository, never()).delete(any());
    }

    // ====================== 测试 updateBookStatus 方法 ======================
    @Test
    public void testUpdateBookStatus_Success() {
        // 1. 准备数据
        BookStatusUpdateDTO dto = new BookStatusUpdateDTO();
        dto.setBookId(bookId);
        dto.setStatus("finished");

        // 2. Mock 依赖
        BookshelfEntity mockShelf = new BookshelfEntity();
        mockShelf.setUserId(userId);
        mockShelf.setBookId(bookId);

        ReadingProgressEntity mockProgress = new ReadingProgressEntity();
        mockProgress.setReadingProgressId(1);

        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(mockShelf));
        when(progressRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(mockProgress));

        // 3. 执行方法
        BookStatusVO result = bookshelfService.updateBookStatus(dto, userId);

        // 4. 验证结果
        assertNotNull(result);
        assertEquals(bookId, result.getBookId());
        assertEquals("finished", result.getStatus());
        assertEquals("阅读状态已更新", result.getMessage());

        // 5. 验证更新操作
        verify(bookshelfRepository, times(1)).updateBookStatus(eq(userId), eq(bookId), eq("finished"),
                any(LocalDateTime.class));
        verify(progressRepository, times(1)).save(mockProgress); // 验证进度表时间更新
    }

    @Test
    public void testUpdateBookStatus_InvalidStatus() {
        // 1. 准备数据（非法状态）
        BookStatusUpdateDTO dto = new BookStatusUpdateDTO();
        dto.setBookId(bookId);
        dto.setStatus("invalid");

        // 2. Mock：书籍在书架
        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(new BookshelfEntity()));

        // 3. 执行并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookshelfService.updateBookStatus(dto, userId);
        });
        assertEquals("状态必须为 reading/unread/finished", exception.getMessage());
    }

    @Test
    public void testUpdateBookStatus_BookNotInShelf() {
        // 1. 准备数据
        BookStatusUpdateDTO dto = new BookStatusUpdateDTO();
        dto.setBookId(bookId);

        // 2. Mock：书籍不在书架
        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.empty());

        // 3. 执行并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookshelfService.updateBookStatus(dto, userId);
        });
        assertEquals("书籍不在书架中", exception.getMessage());
    }

    // ====================== 测试 updateReadingProgress 方法 ======================
    @Test
    public void testUpdateReadingProgress_Success() {
        // 1. 准备数据
        ReadingProgressDTO dto = new ReadingProgressDTO();
        dto.setBookId(bookId);
        dto.setChapterId(5);
        dto.setCurrentPage(120);
        dto.setProgress(0.6f);

        // 2. Mock 依赖
        BookshelfEntity mockShelf = new BookshelfEntity();
        mockShelf.setBookshelfId(1);

        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(mockShelf));

        // 3. 执行方法
        ReadingProgressVO result = bookshelfService.updateReadingProgress(dto, userId);

        // 4. 验证结果
        assertNotNull(result);
        assertEquals(bookId, result.getBookId());
        assertEquals(5, result.getChapterId());
        assertEquals(120, result.getCurrentPage());
        assertEquals(0.6f, result.getProgress());
        assertNotNull(result.getLastReadTime());
        assertEquals("阅读进度已更新", result.getMessage());

        // 5. 验证更新操作
        verify(progressRepository, times(1)).updateProgress(
                eq(userId), eq(bookId), eq(5), eq(120), eq(0.6f), any(LocalDateTime.class));
        verify(bookshelfRepository, times(1)).save(mockShelf); // 验证书架表时间更新
    }

    @Test
    public void testUpdateReadingProgress_InvalidProgress() {
        // 1. 准备数据（进度超出范围）
        ReadingProgressDTO dto = new ReadingProgressDTO();
        dto.setBookId(bookId);
        dto.setProgress(1.2f); // 非法值

        // 2. Mock：书籍在书架
        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(new BookshelfEntity()));

        // 3. 执行并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookshelfService.updateReadingProgress(dto, userId);
        });
        assertEquals("进度值必须在 0-1 之间", exception.getMessage());
    }

    @Test
    public void testUpdateReadingProgress_InvalidPage() {
        // 1. 准备数据（页码非法）
        ReadingProgressDTO dto = new ReadingProgressDTO();
        dto.setBookId(bookId);
        dto.setCurrentPage(0); // 非法值
        dto.setProgress(0.5f);

        // 2. Mock：书籍在书架
        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(new BookshelfEntity()));

        // 3. 执行并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookshelfService.updateReadingProgress(dto, userId);
        });
        assertEquals("页码必须为正整数", exception.getMessage());
    }

    @Test
    public void testUpdateReadingProgress_BookNotInShelf() {
        // 1. 准备数据
        ReadingProgressDTO dto = new ReadingProgressDTO();
        dto.setBookId(bookId);

        // 2. Mock：书籍不在书架
        when(bookshelfRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.empty());

        // 3. 执行并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookshelfService.updateReadingProgress(dto, userId);
        });
        assertEquals("书籍不在书架中，无法更新进度", exception.getMessage());
    }

    // ====================== 测试 getUserBooks 方法 ======================
    @Test
    public void testGetUserBooks_WithStatusFilter() {
        // 1. 准备查询条件
        BookshelfQueryDTO dto = new BookshelfQueryDTO();
        dto.setStatus("reading");

        // 2. Mock 依赖数据
        BookshelfEntity shelf1 = new BookshelfEntity();
        shelf1.setBookshelfId(1);
        shelf1.setUserId(userId);
        shelf1.setBookId(bookId);
        shelf1.setStatus("reading");
        shelf1.setAddedAt(LocalDateTime.now());

        BookEntity book = new BookEntity();
        book.setBookId(bookId);
        book.setTitle("测试书籍");
        book.setAuthorId(authorId);
        book.setCover("cover.jpg");

        AuthorEntity author = new AuthorEntity();
        author.setAuthorId(authorId);
        author.setName("测试作者");

        ReadingProgressEntity progress = new ReadingProgressEntity();
        progress.setChapterId(3);
        progress.setCurrentPage(50);
        progress.setProgress(0.3f);

        when(bookshelfRepository.findByUserIdAndStatus(userId, "reading")).thenReturn(List.of(shelf1));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(progressRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(progress));

        // 3. 执行方法
        List<BookShelfVO> result = bookshelfService.getUserBooks(dto, userId);

        // 4. 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());

        BookShelfVO vo = result.get(0);
        assertEquals(bookId, vo.getBookId());
        assertEquals("测试书籍", vo.getTitle());
        assertEquals("测试作者", vo.getAuthor());
        assertEquals("cover.jpg", vo.getCoverUrl());
        assertEquals("reading", vo.getStatus());
        assertEquals(3, vo.getChapterId());
        assertEquals(50, vo.getCurrentPage());
        assertEquals(0.3f, vo.getProgress());
    }

    @Test
    public void testGetUserBooks_WithoutStatusFilter() {
        // 1. 准备查询条件（不筛选状态）
        BookshelfQueryDTO dto = new BookshelfQueryDTO();

        // 2. Mock 依赖数据（返回2本书）
        BookshelfEntity shelf1 = new BookshelfEntity();
        shelf1.setBookId(bookId);
        shelf1.setStatus("reading");

        BookshelfEntity shelf2 = new BookshelfEntity();
        shelf2.setBookId(1002); // 第二本书ID
        shelf2.setStatus("finished");

        // 为两本书设置有效的 authorId（与 stub 一致）
        BookEntity book1 = new BookEntity();
        book1.setBookId(bookId);
        book1.setAuthorId(authorId); // 使用测试类中定义的 authorId=2001

        BookEntity book2 = new BookEntity();
        book2.setBookId(1002);
        book2.setAuthorId(authorId); // 统一使用同一个 authorId

        // 修正 stub：当查询 authorId=2001 时返回 mock 作者
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(1002)).thenReturn(Optional.of(book2));
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(new AuthorEntity())); // 匹配 authorId=2001
        when(progressRepository.findByUserIdAndBookId(anyInt(), anyInt()))
                .thenReturn(Optional.of(new ReadingProgressEntity()));

        // 3. 执行方法
        List<BookShelfVO> result = bookshelfService.getUserBooks(dto, userId);

        // 4. 验证结果
        assertEquals(2, result.size()); // 验证返回2条数据
    }

    @Test
    public void testGetUserBooks_NoBooks() {
        // 1. 准备查询条件
        BookshelfQueryDTO dto = new BookshelfQueryDTO();

        // 2. Mock：用户无书架书籍
        when(bookshelfRepository.findByUserId(userId)).thenReturn(List.of());

        // 3. 执行方法
        List<BookShelfVO> result = bookshelfService.getUserBooks(dto, userId);

        // 4. 验证结果
        assertTrue(result.isEmpty());
    }
}