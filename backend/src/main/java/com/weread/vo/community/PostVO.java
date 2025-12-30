package com.weread.vo.community;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import com.weread.vo.user.UserSummaryVO;
import com.weread.vo.book.BookSummaryVO;

@Data
public class PostVO {
    private Integer postId;
    private Integer authorId;
    private String title;
    // 列表页只返回部分内容，详情页返回全部
    private String contentSummary; 
    private List<BookSummaryVO> relatedBooks;
    private List<String> hashtags;
    private long likesCount;
    private long commentsCount;

    /**
     * 点赞该帖子的前 N 个用户的简要信息列表。
     * 用于在帖子列表中直接展示部分点赞用户。
     */
    private List<UserSummaryVO> topLikers;

    private List<TopicVO> topics; // 帖子关联的话题列表
    
    private LocalDateTime createdAt;
    
    
}