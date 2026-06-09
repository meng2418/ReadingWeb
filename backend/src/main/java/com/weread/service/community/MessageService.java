package com.weread.service.community;

import java.util.Map;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.weread.entity.community.CommentEntity;
import com.weread.entity.community.LikeEntity;
import com.weread.entity.community.PostEntity;

public interface MessageService {
    
    /**
     * 获取我的帖子的评论瀑布流
     */
    Map<String, Object> getMyPostsComments(Integer userId, Integer cursor, Integer limit);
    
    /**
     * 获取我的点赞瀑布流
     */
    Map<String, Object> getMyLikes(Integer userId, Integer cursor, Integer limit);

    Map<String, Object> buildCommentNotification(Integer commentId);

    Map<String, Object> buildLikeNotification(Integer likeId);

    SseEmitter subscribeNotifications(Integer userId);

    void notifyPostComment(CommentEntity comment, PostEntity post);

    void notifyPostLike(LikeEntity like, PostEntity post);

    void notifyCommentLike(LikeEntity like, CommentEntity comment);
}