package com.weread.service.user;

import com.weread.dto.reading.RecentBookDTO;
import java.util.List;

public interface RecentBookService {
    /**
     * 获取用户最近阅读的书籍
     * 
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 最近阅读的书籍列表
     */
    List<RecentBookDTO> getRecentBooks(Long userId, int limit);
}