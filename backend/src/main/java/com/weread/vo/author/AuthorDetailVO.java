package com.weread.vo.author;

import lombok.Data;
import java.util.List;

/**
 * 作者详情VO
 */
@Data
public class AuthorDetailVO {
    
    /**
     * 作者ID
     */
    private Long authorId;
    
    /**
     * 作者姓名
     */
    private String authorName;
    
    /**
     * 作者简介
     */
    private String authorBio;
    
    /**
     * 关注者数量
     */
    private Integer followerCount;
    
    /**
     * 当前用户是否关注该作者
     */
    private Boolean isFollowing;
    
    /**
     * 作品列表
     */
    private List<AuthorWorkVO> works;
    
    /**
     * 作品数量
     */
    private Integer workCount;
    
    /**
     * 作者作品VO
     */
    @Data
    public static class AuthorWorkVO {
        /**
         * 书籍ID
         */
        private Integer bookId;
        
        /**
         * 书籍标题
         */
        private String bookTitle;
        
        /**
         * 作者姓名
         */
        private String authorName;
        
        /**
         * 封面
         */
        private String cover;
        
        /**
         * 评分
         */
        private Integer rating;
        
        /**
         * 阅读量
         */
        private Integer readCount;
        
        /**
         * 描述
         */
        private String description;
    }
}

