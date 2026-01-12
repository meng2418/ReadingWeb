package com.weread.service.impl.book;

import com.weread.service.book.RecommendationService;
import com.weread.dto.book.SimpleBookDTO;
import com.weread.entity.book.BookEntity;
import com.weread.repository.book.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationServiceImpl implements RecommendationService {

    private final BookRepository bookRepository;
    private static final int RECOMMENDATION_COUNT = 6;

    @Override
    public List<SimpleBookDTO> getRecommendations() {
        try {
            // 方法1：直接使用数据库随机（最简单）
            List<BookEntity> randomBooks = bookRepository.findRandomBooks();

            // 如果数据库返回不足6本（比如书籍总数不够），查询所有书籍
            if (randomBooks.size() < RECOMMENDATION_COUNT) {
                log.info("书籍数量不足{}本，返回所有{}本书籍",
                        RECOMMENDATION_COUNT, randomBooks.size());
            }

            return convertToDTOs(randomBooks);

        } catch (Exception e) {
            log.error("获取推荐书籍失败: {}", e.getMessage());
            // 异常时返回空列表，避免500错误
            return Collections.emptyList();
        }
    }

    /**
     * 转换为DTO列表
     */
    private List<SimpleBookDTO> convertToDTOs(List<BookEntity> books) {
        return books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 单个实体转DTO
     */
    private SimpleBookDTO convertToDTO(BookEntity book) {
        SimpleBookDTO dto = new SimpleBookDTO();
        dto.setBookId(book.getBookId());
        dto.setBookTitle(book.getTitle());
        dto.setAuthorName(book.getAuthor() != null ? book.getAuthor().getAuthorName() : "未知作者");
        // 处理cover路径：如果是相对路径（如"1_cover.jpeg"），拼接为完整路径
        String cover = book.getCoverImage();
        if (cover != null && !cover.startsWith("/") && !cover.startsWith("http")) {
            cover = "/static/images/" + cover;
        }
        dto.setCover(cover);
        dto.setDescription(book.getDescription() != null ? book.getDescription() : "");
        dto.setReadingStatus("unread"); // 推荐书籍默认未读
        return dto;
    }
}