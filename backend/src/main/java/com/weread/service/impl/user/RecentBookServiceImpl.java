package com.weread.service.impl.user;

import com.weread.dto.reading.RecentBookDTO;
import com.weread.entity.book.BookEntity;
import com.weread.repository.book.BookRepository;
import com.weread.repository.user.UserReadingRecordRepository;
import com.weread.repository.user.UserReadingRecordRepository;
import com.weread.service.user.RecentBookService;
import com.weread.util.ImagePathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecentBookServiceImpl implements RecentBookService {

    @Autowired
    private UserReadingRecordRepository recentBookRepository;

    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<RecentBookDTO> getRecentBooks(Integer userId, int limit) {
        // 使用原生SQL或修改返回值处理
        List<Object[]> results = recentBookRepository.findRecentBookIdsWithMaxDate(userId);

        if (CollectionUtils.isEmpty(results)) {
            return new ArrayList<>();
        }

        // 2. 限制数量（取前limit个）
        List<Integer> bookIds = new ArrayList<>();
        for (int i = 0; i < Math.min(results.size(), limit); i++) {
            Object[] row = results.get(i);
            Integer bookId = (Integer) row[0]; // 第一个元素是bookId
            bookIds.add(bookId);
        }

        // 3. 批量查询这些书籍的信息
        List<BookEntity> books = bookRepository.findByBookIds(bookIds);

        // 4. 创建书籍ID到封面的映射
        Map<Integer, String> coverMap = new HashMap<>();
        for (BookEntity book : books) {
            if (book != null) {
                String cover = ImagePathUtils.processCoverPathWithDefault(
                    book.getCover(), getDefaultCover());
                coverMap.put(book.getBookId(), cover);
            }
        }

        // 5. 按原顺序转换为DTO（保持时间排序）
        List<RecentBookDTO> result = new ArrayList<>();
        for (Integer bookId : bookIds) {
            String cover = coverMap.get(bookId);
            if (cover == null) {
                cover = getDefaultCover();
            }
            result.add(new RecentBookDTO(bookId, cover));
        }

        return result;
    }

    private String getDefaultCover() {
        return "/static/images/default-book-cover.jpg";
    }
}