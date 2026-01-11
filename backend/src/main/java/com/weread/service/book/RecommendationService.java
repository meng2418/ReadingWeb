package com.weread.service.book;

import com.weread.dto.book.SimpleBookDTO;

import java.util.List;

/**
 * 推荐服务接口
 */
public interface RecommendationService {

    /**
     * 获取猜你想看书籍（固定6本）
     * 
     * @return 6本推荐书籍
     */
    List<SimpleBookDTO> getRecommendations();
}
