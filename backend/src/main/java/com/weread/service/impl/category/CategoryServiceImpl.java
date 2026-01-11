package com.weread.service.impl.category;

import com.weread.dto.book.SimpleBookDTO;
import com.weread.entity.book.BookEntity;
import com.weread.repository.book.BookRepository;
import com.weread.repository.category.CategoryRepository;
import com.weread.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Map<String, Object> getCategoryBooks(Integer categoryId, Integer page, Integer limit) {
        log.debug("获取分类书籍 - categoryId: {}, page: {}, limit: {}", categoryId, page, limit);

        // 1. 参数验证
        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException("分类ID无效");
        }

        // 2. 验证分类是否存在
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("分类不存在: " + categoryId);
        }

        // 3. 限制参数范围
        int validLimit = Math.min(limit, 50);
        int validPage = Math.max(page, 1);

        // 4. 创建分页请求（按阅读量倒序，再按评分倒序）
        Pageable pageable = PageRequest.of(validPage - 1, validLimit,
                Sort.by(Sort.Direction.DESC, "readCount")
                        .and(Sort.by(Sort.Direction.DESC, "rating")));

        // 5. 使用你已有的方法查询
        Page<BookEntity> bookPage = bookRepository.findByCategoryIdAndIsPublishedTrue(categoryId, pageable);

        // 6. 转换为DTO
        List<SimpleBookDTO> bookDTOs = bookPage.getContent().stream()
                .map(this::convertToSimpleBookDTO)
                .collect(Collectors.toList());

        // 7. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("books", bookDTOs);
        result.put("total", bookPage.getTotalElements());
        result.put("page", validPage);
        result.put("limit", validLimit);
        result.put("totalPages", bookPage.getTotalPages());
        result.put("hasNext", bookPage.hasNext());
        result.put("hasPrevious", bookPage.hasPrevious());

        // 8. 添加分类信息（可选）
        categoryRepository.findById(categoryId).ifPresent(category -> {
            result.put("categoryName", category.getName());
            result.put("categoryDescription", category.getDescription());
        });

        log.info("成功获取分类书籍 - 分类ID: {}, 总数: {}, 当前页: {} 条",
                categoryId, bookPage.getTotalElements(), bookDTOs.size());

        return result;
    }

    /**
     * 转换为SimpleBookDTO
     */
    private SimpleBookDTO convertToSimpleBookDTO(BookEntity book) {
        SimpleBookDTO dto = new SimpleBookDTO();
        dto.setBookId(book.getBookId());
        dto.setBookTitle(book.getTitle());

        // 处理作者信息
        dto.setAuthor(getAuthorName(book));

        dto.setCover(book.getCover());
        dto.setReadingStatus("unread"); // 默认未读

        // 修复：使用ifPresent的正确方式
        if (book.getRating() != null) {
            dto.setRating(book.getRating().floatValue()); // Float
        }

        if (book.getReadCount() != null) {
            dto.setReadCount(book.getReadCount());
        }

        return dto;
    }

    /**
     * 获取作者名称（处理空值）
     */
    private String getAuthorName(BookEntity book) {
        if (book.getAuthor() != null && book.getAuthor().getAuthorName() != null) {
            return book.getAuthor().getAuthorName();
        }
        // 如果Author实体没有name字段，可能有authorName字段
        if (book.getAuthorName() != null) {
            return book.getAuthorName();
        }
        return "未知作者";
    }
}