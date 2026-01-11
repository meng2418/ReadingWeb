package com.weread.service.list;

import com.weread.dto.book.SimpleBookDTO;

import java.util.List;

/**
 * 榜单服务接口
 */
public interface RankingService {

    /**
     * 获取榜单书籍
     * 
     * @param type 榜单类型 weekly-周榜, monthly-月榜, new-新书榜, masterpiece-神作榜
     * @return 10本榜单书籍
     */
    List<SimpleBookDTO> getRankingBooks(String type);

    /**
     * 周榜：综合近期热度和质量
     */
    List<SimpleBookDTO> getWeeklyRanking();

    /**
     * 月榜：综合长期热度和质量
     */
    List<SimpleBookDTO> getMonthlyRanking();

    /**
     * 新书榜：最近3个月的新书，按推荐值排序
     */
    List<SimpleBookDTO> getNewBooksRanking();

    /**
     * 神作榜：经典高质量书籍，重质量和口碑
     */
    List<SimpleBookDTO> getMasterpieceRanking();
}