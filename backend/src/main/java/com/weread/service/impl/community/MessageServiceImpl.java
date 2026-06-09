package com.weread.service.impl.community;

import com.weread.entity.community.CommentEntity;
import com.weread.entity.community.LikeEntity;
import com.weread.entity.community.PostEntity;
import com.weread.entity.user.UserEntity;
import com.weread.repository.community.CommentRepository;
import com.weread.repository.community.LikeRepository;
import com.weread.repository.community.PostRepository;
import com.weread.repository.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weread.service.community.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    
    private static final long SSE_TIMEOUT_MS = 30L * 60 * 1000;

    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    private final ConcurrentHashMap<Integer, CopyOnWriteArrayList<SseEmitter>> notificationEmitters =
            new ConcurrentHashMap<>();

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
        
        // 设置nextCursor
        Integer nextCursor = null;
        if (!likeList.isEmpty() && likeList.size() >= limit) {
            Map<String, Object> lastLike = likeList.get(likeList.size() - 1);
            Object likeIdObj = lastLike.get("likeId");
            if (likeIdObj instanceof Integer) {
                nextCursor = (Integer) likeIdObj;
            } else if (likeIdObj instanceof Long) {
                nextCursor = ((Long) likeIdObj).intValue();
            }
        }
        result.put("nextCursor", nextCursor);
        
        return result;
    }
    
    private Map<String, Object> convertCommentToMap(CommentEntity comment) {
        Map<String, Object> map = new HashMap<>();
        map.put("commentId", comment.getCommentId());
        map.put("postId", comment.getPostId());
        map.put("commentContent", comment.getContent() != null ? comment.getContent() : "");
        map.put("commentTime", comment.getCreatedAt());
        
        // 获取帖子信息
        try {
            Optional<PostEntity> postOpt = postRepository.findById(comment.getPostId());
            postOpt.ifPresent(post -> {
                map.put("postTitle", post.getTitle() != null ? post.getTitle() : "");
            });
            if (!postOpt.isPresent()) {
                map.put("postTitle", "");
            }
        } catch (Exception e) {
            log.warn("获取帖子信息失败: postId={}", comment.getPostId(), e);
            map.put("postTitle", "");
        }
        
        // 获取评论者信息
        try {
            Optional<UserEntity> commenterOpt = userRepository.findById(comment.getUserId());
            commenterOpt.ifPresent(user -> {
                Map<String, Object> commenter = new HashMap<>();
                commenter.put("userId", user.getUserId());
                commenter.put("username", user.getUsername() != null ? user.getUsername() : "");
                commenter.put("avatar", user.getAvatar() != null ? user.getAvatar() : "");
                map.put("commenter", commenter);
            });
            if (!commenterOpt.isPresent()) {
                Map<String, Object> commenter = new HashMap<>();
                commenter.put("userId", 0);
                commenter.put("username", "");
                commenter.put("avatar", "");
                map.put("commenter", commenter);
            }
        } catch (Exception e) {
            log.warn("获取评论者信息失败: userId={}", comment.getUserId(), e);
            Map<String, Object> commenter = new HashMap<>();
            commenter.put("userId", 0);
            commenter.put("username", "");
            commenter.put("avatar", "");
            map.put("commenter", commenter);
        }
        
        // 父评论信息
        if (comment.getParentCommentId() != null) {
            map.put("parentCommentId", comment.getParentCommentId());
        }
        
        return map;
    }
    
    private Map<String, Object> convertLikeToMap(LikeEntity like) {
        Map<String, Object> map = new HashMap<>();
        map.put("likeId", like.getLikeId());
        map.put("targetType", like.getTargetType() != null ? like.getTargetType() : "post");
        map.put("likeTime", like.getCreatedAt());
        
        // 获取点赞者信息
        try {
            Optional<UserEntity> likerOpt = userRepository.findById(like.getUserId());
            likerOpt.ifPresent(user -> {
                Map<String, Object> liker = new HashMap<>();
                liker.put("userId", user.getUserId());
                liker.put("username", user.getUsername() != null ? user.getUsername() : "");
                liker.put("avatar", user.getAvatar() != null ? user.getAvatar() : "");
                map.put("liker", liker);
            });
            if (!likerOpt.isPresent()) {
                Map<String, Object> liker = new HashMap<>();
                liker.put("userId", 0);
                liker.put("username", "");
                liker.put("avatar", "");
                map.put("liker", liker);
            }
        } catch (Exception e) {
            log.warn("获取点赞者信息失败: userId={}", like.getUserId(), e);
            Map<String, Object> liker = new HashMap<>();
            liker.put("userId", 0);
            liker.put("username", "");
            liker.put("avatar", "");
            map.put("liker", liker);
        }
        
        // 根据点赞类型处理不同的ID字段
        if ("post".equals(like.getTargetType())) {
            Integer postId = like.getPostId();
            map.put("targetId", postId != null ? postId : 0);
            
            try {
                if (postId != null) {
                    Optional<PostEntity> postOpt = postRepository.findById(postId);
                    postOpt.ifPresent(post -> {
                        map.put("postTitle", post.getTitle() != null ? post.getTitle() : "");
                    });
                    if (!postOpt.isPresent()) {
                        map.put("postTitle", "");
                    }
                } else {
                    map.put("postTitle", "");
                }
            } catch (Exception e) {
                log.warn("获取帖子信息失败: postId={}", postId, e);
                map.put("postTitle", "");
            }
            
        } else if ("comment".equals(like.getTargetType())) {
            Integer commentId = like.getCommentId();
            map.put("targetId", commentId != null ? commentId : 0);
            
            try {
                if (commentId != null) {
                    Optional<CommentEntity> commentOpt = commentRepository.findById(commentId);
                    commentOpt.ifPresent(comment -> {
                        map.put("commentContent", comment.getContent() != null ? comment.getContent() : "");
                        // 同时获取相关帖子标题
                        if (comment.getPostId() != null) {
                            Optional<PostEntity> postOpt = postRepository.findById(comment.getPostId());
                            postOpt.ifPresent(post -> {
                                map.put("postTitle", post.getTitle() != null ? post.getTitle() : "");
                            });
                            if (!postOpt.isPresent()) {
                                map.put("postTitle", "");
                            }
                        } else {
                            map.put("postTitle", "");
                        }
                    });
                    if (!commentOpt.isPresent()) {
                        map.put("commentContent", "");
                        map.put("postTitle", "");
                    }
                } else {
                    map.put("commentContent", "");
                    map.put("postTitle", "");
                }
            } catch (Exception e) {
                log.warn("获取评论信息失败: commentId={}", commentId, e);
                map.put("commentContent", "");
                map.put("postTitle", "");
            }
        } else {
            map.put("targetId", 0);
            map.put("postTitle", "");
        }
        
        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> buildCommentNotification(Integer commentId) {
        if (commentId == null) {
            return Collections.emptyMap();
        }
        return commentRepository.findById(commentId)
                .map(this::convertCommentToMap)
                .orElse(Collections.emptyMap());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> buildLikeNotification(Integer likeId) {
        if (likeId == null) {
            return Collections.emptyMap();
        }
        return likeRepository.findById(likeId.longValue())
                .map(this::convertLikeToMap)
                .orElse(Collections.emptyMap());
    }

    @Override
    public SseEmitter subscribeNotifications(Integer userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        notificationEmitters.computeIfAbsent(userId, key -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> removeNotificationEmitter(userId, emitter));
        emitter.onTimeout(() -> removeNotificationEmitter(userId, emitter));
        emitter.onError(ex -> removeNotificationEmitter(userId, emitter));
        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException ex) {
            removeNotificationEmitter(userId, emitter);
        }
        return emitter;
    }

    @Override
    public void notifyPostComment(CommentEntity comment, PostEntity post) {
        if (comment == null || post == null || post.getAuthorId() == null) {
            return;
        }
        if (post.getAuthorId().equals(comment.getUserId())) {
            return;
        }
        String actorName = resolveNotificationUsername(comment.getUserId());
        Map<String, Object> event = new HashMap<>();
        event.put("type", "comment");
        event.put("title", "\u65b0\u8bc4\u8bba");
        event.put("message", actorName + " \u8bc4\u8bba\u4e86\u4f60\u7684\u5e16\u5b50");
        event.put("data", buildCommentNotification(comment.getCommentId()));
        publishNotification(post.getAuthorId(), event);
    }

    @Override
    public void notifyPostLike(LikeEntity like, PostEntity post) {
        if (like == null || post == null || post.getAuthorId() == null) {
            return;
        }
        if (post.getAuthorId().equals(like.getUserId())) {
            return;
        }
        String actorName = resolveNotificationUsername(like.getUserId());
        Map<String, Object> event = new HashMap<>();
        event.put("type", "like");
        event.put("title", "\u65b0\u70b9\u8d5e");
        event.put("message", actorName + " \u8d5e\u4e86\u4f60\u7684\u5e16\u5b50");
        event.put("data", buildLikeNotification(like.getLikeId()));
        publishNotification(post.getAuthorId(), event);
    }

    @Override
    public void notifyCommentLike(LikeEntity like, CommentEntity comment) {
        if (like == null || comment == null || comment.getUserId() == null) {
            return;
        }
        if (comment.getUserId().equals(like.getUserId())) {
            return;
        }
        String actorName = resolveNotificationUsername(like.getUserId());
        Map<String, Object> event = new HashMap<>();
        event.put("type", "like");
        event.put("title", "\u65b0\u70b9\u8d5e");
        event.put("message", actorName + " \u8d5e\u4e86\u4f60\u7684\u8bc4\u8bba");
        event.put("data", buildLikeNotification(like.getLikeId()));
        publishNotification(comment.getUserId(), event);
    }

    private String resolveNotificationUsername(Integer userId) {
        return userRepository.findById(userId)
                .map(UserEntity::getUsername)
                .orElse("\u6709\u4eba");
    }

    private void publishNotification(Integer userId, Map<String, Object> event) {
        List<SseEmitter> connections = notificationEmitters.get(userId);
        if (connections == null || connections.isEmpty()) {
            return;
        }
        try {
            String payload = objectMapper.writeValueAsString(event);
            for (SseEmitter emitter : connections) {
                try {
                    emitter.send(SseEmitter.event().name("notification").data(payload));
                } catch (IOException ex) {
                    removeNotificationEmitter(userId, emitter);
                }
            }
        } catch (IOException ex) {
            log.warn("Failed to serialize notification event: {}", ex.getMessage());
        }
    }

    private void removeNotificationEmitter(Integer userId, SseEmitter emitter) {
        List<SseEmitter> connections = notificationEmitters.get(userId);
        if (connections != null) {
            connections.remove(emitter);
            if (connections.isEmpty()) {
                notificationEmitters.remove(userId);
            }
        }
        try {
            emitter.complete();
        } catch (Exception ignored) {
            // ignore
        }
    }
    
}