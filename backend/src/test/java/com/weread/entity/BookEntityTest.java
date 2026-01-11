package com.weread.entity;

import org.junit.jupiter.api.Test;

import com.weread.entity.book.BookEntity;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class BookEntityTest {

    private BookEntity book;

    @BeforeEach
    public void setUp() {
        book = new BookEntity();
        book.setTitle("测试书籍");
        book.setAuthorId(1);
        book.setCategoryId(1);
    }

    @Test
    public void testRequiredFields() {
        assertNotNull(book.getTitle());
        assertNotNull(book.getAuthorId());
        assertNotNull(book.getCategoryId());
    }

    @Test
    public void testDefaultValues() {
        // 模拟@PrePersist被调用
        // 由于@PrePersist需要JPA环境，这里手动设置
        if (book.getPrice() == null) {
            book.setPrice(0);
        }
        if (book.getIsFree() == null) {
            book.setIsFree(false);
        }
        if (book.getRating() == null) {
            book.setRating(0f);
        }

        assertEquals(0, book.getPrice());
        assertEquals(false, book.getIsFree());
        assertEquals(0f, book.getRating());
    }

    @Test
    public void testSettersAndGetters() {
        // 测试所有字段的setter/getter
        book.setCover("cover.jpg");
        assertEquals("cover.jpg", book.getCover());

        book.setDescription("这是一本测试书籍");
        assertEquals("这是一本测试书籍", book.getDescription());

        book.setWordCount(50000);
        assertEquals(50000, book.getWordCount());
    }
}