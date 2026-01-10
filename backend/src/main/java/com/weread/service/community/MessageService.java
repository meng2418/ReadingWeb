package com.weread.service.community;

import java.util.Map;

public interface MessageService {
    
    /**
     * 获取我的帖子的评论瀑布流
     */
    Map<String, Object> getMyPostsComments(Integer userId, Integer cursor, Integer limit);
    
    /**
     * 获取我的点赞瀑布流
     */
    Map<String, Object> getMyLikes(Integer userId, Integer cursor, Integer limit);
}