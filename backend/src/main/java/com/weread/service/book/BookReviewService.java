package com.weread.service.book;

import com.weread.dto.book.BookReviewCreateDTO;
import com.weread.dto.book.RecentBookReviewDTO;
import com.weread.dto.book.UserBookReviewsResponseDTO;
import com.weread.vo.book.BookReviewVO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 书评服务接口
 */
public interface BookReviewService {

    /**
     * 创建书评
     */
    BookReviewVO createReview(Integer userId, BookReviewCreateDTO dto);

    /**
     * 删除书评（只能删除自己的）
     * @return 删除后用户剩余的书评数量
     */
    Integer deleteReview(Integer userId, Integer reviewId);

    /**
     * 获取书籍的书评列表
     */
    Page<BookReviewVO> getBookReviews(Integer bookId, Integer page, Integer size);

    /**
     * 获取书籍的书评列表（限制数量，用于书籍详情页）
     */
    List<BookReviewVO> getBookReviewsLimited(Integer bookId, Integer limit);

    /**
     * 获取书籍的评分统计
     */
    BookReviewService.RatingStats getRatingStats(Integer bookId);

    /**
     * 获取用户的书评列表（限制数量，用于个人中心）
     */
    List<BookReviewVO> getUserReviewsLimited(Integer userId, Integer limit);

    /**
     * 获取用户的书评瀑布流（游标分页）
     */
    UserBookReviewsResponseDTO getUserReviewsWithCursor(Integer userId, Integer cursor, Integer limit);
    
    /**
     * 获取用户最新的书评列表（用于个人中心，返回RecentBookReviewDTO格式）
     */
    List<RecentBookReviewDTO> getUserRecentReviews(Integer userId, Integer limit);
    
    /**
     * 评分统计
     */
    class RatingStats {
        private Long recommend = 0L;
        private Long average = 0L;
        private Long notRecommend = 0L;
        private Long total = 0L;

        public Long getRecommend() {
            return recommend;
        }

        public void setRecommend(Long recommend) {
            this.recommend = recommend;
        }

        public Long getAverage() {
            return average;
        }

        public void setAverage(Long average) {
            this.average = average;
        }

        public Long getNotRecommend() {
            return notRecommend;
        }

        public void setNotRecommend(Long notRecommend) {
            this.notRecommend = notRecommend;
        }

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }
    }
}

