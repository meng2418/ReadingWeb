package com.weread.service.impl.community;

import com.weread.entity.community.CommentEntity;
import com.weread.entity.community.LikeEntity;
import com.weread.entity.community.PostEntity;
import com.weread.entity.user.UserEntity;
import com.weread.repository.community.CommentRepository;
import com.weread.repository.community.LikeRepository;
import com.weread.repository.community.PostRepository;
import com.weread.repository.user.UserRepository;
import com.weread.service.community.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
@Transactional(readOnly = true)
public Map<String, Object> getMyPostsComments(Integer userId, Integer cursor, Integer limit) {
    
    // 参数验证
    if (userId == null) {
        log.warn("用户ID为空，返回空结果");
        return buildEmptyResult();
    }
    
    // 1. 获取当前用户发布的所有帖子ID
    List<Integer> myPostIds = postRepository.findPostIdsByUserId(userId);
    
    if (myPostIds.isEmpty()) {
        return buildEmptyResult();
    }
    
    // 2. 构建分页和查询条件
    Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
    
    List<CommentEntity> comments;
    if (cursor != null) {
        comments = commentRepository.findCommentsOnMyPostsAfterCursor(
            myPostIds, cursor, pageable);
    } else {
        comments = commentRepository.findCommentsOnMyPosts(
            myPostIds, userId, pageable);
    }
    
    // 3. 转换为响应格式
    List<Map<String, Object>> commentList = comments.stream()
        .map(this::convertCommentToMap)
        .collect(Collectors.toList());
    
    // 4. 构建结果
    Map<String, Object> result = new HashMap<>();
    result.put("comments", commentList);
    
    // 判断是否有更多数据
    boolean hasMore = commentList.size() >= limit;
    result.put("hasMore", hasMore);
    
    // 设置nextCursor - 始终返回0
    Integer nextCursor = 0;
    if (hasMore && !commentList.isEmpty()) {
        Map<String, Object> lastComment = commentList.get(commentList.size() - 1);
        nextCursor = (Integer) lastComment.get("commentId");
    }
    result.put("nextCursor", nextCursor);  // 有数据时返回最后一条的ID，无数据时返回0
    
    return result;
}

private Map<String, Object> buildEmptyResult() {
    Map<String, Object> result = new HashMap<>();
    result.put("comments", Collections.emptyList());
    result.put("hasMore", false);
    result.put("nextCursor", 0);  // 直接返回0
    return result;
}

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMyLikes(Integer userId, Integer cursor, Integer limit) {
        log.info("获取用户 {} 的点赞列表，cursor: {}, limit: {}", userId, cursor, limit);
        
        // 参数验证
        if (userId == null) {
            log.warn("用户ID为空，返回空结果");
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("likes", Collections.emptyList());
            emptyResult.put("hasMore", false);
            emptyResult.put("nextCursor", null);
            return emptyResult;
        }
        
        // 1. 构建分页
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        
        List<LikeEntity> likes;
        if (cursor != null) {
            // 基于游标查询
            Integer cursorId = cursor;
            likes = likeRepository.findLikesForMyContentAfterCursor(
                userId, cursorId, pageable);
        } else {
            // 第一次查询
            likes = likeRepository.findLikesForMyContent(userId, pageable);
        }
        
        // 2. 转换为响应格式
        List<Map<String, Object>> likeList = new ArrayList<>();
        for (LikeEntity like : likes) {
            try {
                Map<String, Object> likeMap = convertLikeToMap(like);
                likeList.add(likeMap);
            } catch (Exception e) {
                log.warn("转换点赞记录失败: {}", like.getLikeId(), e);
            }
        }
        
        // 3. 构建结果
        Map<String, Object> result = new HashMap<>();
        result.put("likes", likeList);
        result.put("hasMore", likeList.size() >= limit);
        result.put("nextCursor", likeList.isEmpty() ? null : 
            String.valueOf(likeList.get(likeList.size() - 1).get("likeId")));
        
        return result;
    }
    
    private Map<String, Object> convertCommentToMap(CommentEntity comment) {
        Map<String, Object> map = new HashMap<>();
        map.put("commentId", comment.getCommentId());
        map.put("postId", comment.getPostId());
        map.put("commentContent", comment.getContent());
        map.put("commentTime", comment.getCreatedAt());
        
        // 获取帖子信息
        Optional<PostEntity> postOpt = postRepository.findById(comment.getPostId());
        postOpt.ifPresent(post -> {
            map.put("postTitle", post.getTitle());
        });
        
        // 获取评论者信息
        Optional<UserEntity> commenterOpt = userRepository.findById(comment.getUserId());
        commenterOpt.ifPresent(user -> {
            Map<String, Object> commenter = new HashMap<>();
            commenter.put("userId", user.getUserId());
            commenter.put("username", user.getUsername());
            commenter.put("avatar", user.getAvatar());
            map.put("commenter", commenter);
        });
        
        // 父评论信息
        if (comment.getParentCommentId() != null) {
            map.put("parentCommentId", comment.getParentCommentId());
            Optional<CommentEntity> parentComment = commentRepository.findById(comment.getParentCommentId());
            parentComment.ifPresent(parent -> {
                map.put("parentCommentContent", parent.getContent());
            });
        }
        
        return map;
    }
    
    private Map<String, Object> convertLikeToMap(LikeEntity like) {
        Map<String, Object> map = new HashMap<>();
        map.put("likeId", like.getLikeId());
        map.put("targetType", like.getTargetType()); // "post" 或 "comment"
        map.put("likeTime", like.getCreatedAt());
        
        // 获取点赞者信息
        Optional<UserEntity> likerOpt = userRepository.findById(like.getUserId());
        likerOpt.ifPresent(user -> {
            Map<String, Object> liker = new HashMap<>();
            liker.put("userId", user.getUserId());
            liker.put("username", user.getUsername());
            liker.put("avatar", user.getAvatar());
            map.put("liker", liker);
        });
        
        // 根据点赞类型处理不同的ID字段
    if ("post".equals(like.getTargetType())) {
        Integer postId = like.getPostId();
        map.put("targetId", postId);  // 对应接口文档的 targetId
        
        Optional<PostEntity> postOpt = postRepository.findById(postId);
        postOpt.ifPresent(post -> {
            map.put("postTitle", post.getTitle());
        });
        
    } else if ("comment".equals(like.getTargetType())) {
        Integer commentId = like.getCommentId();
        map.put("targetId", commentId);  // 对应接口文档的 targetId
        
        Optional<CommentEntity> commentOpt = commentRepository.findById(commentId);
        commentOpt.ifPresent(comment -> {
            map.put("commentContent", comment.getContent());
            // 同时获取相关帖子标题
            Optional<PostEntity> postOpt = postRepository.findById(comment.getPostId());
            postOpt.ifPresent(post -> {
                map.put("postTitle", post.getTitle());
            });
        });
    }
        
        return map;
    }
    
}