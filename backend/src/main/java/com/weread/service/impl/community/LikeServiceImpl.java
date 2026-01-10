package com.weread.service.impl.community;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.weread.dto.community.LikeDetailResponseDTO;
import com.weread.dto.community.LikeRequestDTO;
import com.weread.dto.community.LikeResponseDTO;
import com.weread.dto.community.LikedUserDTO;
import com.weread.entity.community.CommentEntity;
import com.weread.entity.community.LikeEntity;
import com.weread.entity.community.PostEntity;
import com.weread.entity.user.UserEntity;
import com.weread.repository.community.CommentRepository;
import com.weread.repository.community.LikeRepository;
import com.weread.repository.community.PostRepository;
import com.weread.repository.user.UserRepository;
import com.weread.service.community.LikeService;


@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public LikeServiceImpl(LikeRepository likeRepository, 
                          PostRepository postRepository,
                          UserRepository userRepository,
                          CommentRepository commentRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public LikeResponseDTO togglePostLike(Integer postId, Integer userId) {
        // 验证帖子存在
        PostEntity post = postRepository.findById(postId)
            .orElseThrow();
        
        // 检查是否已点赞
        Optional<LikeEntity> existingLike = likeRepository.findByPostIdAndUserId(postId, userId);
        
        if (existingLike.isPresent()) {
            // 取消点赞
            likeRepository.delete(existingLike.get());
            post.setLikesCount(post.getLikesCount() - 1);
        } else {
            // 添加点赞
            LikeEntity like = new LikeEntity();
            like.setPostId(postId);
            like.setUserId(userId);
            likeRepository.save(like);
            post.setLikesCount(post.getLikesCount() + 1);
        }
        
        postRepository.save(post);
        
        return new LikeResponseDTO(
            !existingLike.isPresent(), // 新的点赞状态
            post.getLikesCount()
        );
    }

    @Override
    @Transactional
    public LikeResponseDTO toggleCommentLike(Integer commentId, Integer userId) {
        // 验证评论存在
        CommentEntity comment = commentRepository.findById(commentId)
            .orElseThrow();
        
        // 检查是否已点赞
        Optional<LikeEntity> existingLike = likeRepository.findByCommentIdAndUserId(commentId, userId);
        
        if (existingLike.isPresent()) {
            // 取消点赞
            likeRepository.delete(existingLike.get());
            comment.setLikesCount(comment.getLikesCount() - 1);
        } else {
            // 添加点赞
            LikeEntity like = new LikeEntity();
            like.setCommentId(commentId);
            like.setUserId(userId);
            likeRepository.save(like);
            comment.setLikesCount(comment.getLikesCount() + 1);
        }
        
        commentRepository.save(comment);
        
        return new LikeResponseDTO(
            !existingLike.isPresent(),
            comment.getLikesCount()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LikeDetailResponseDTO getPostLikeDetails(Integer postId, Integer userId) {
        // 验证帖子存在并获取作者ID
        PostEntity post = postRepository.findById(postId)
            .orElseThrow();
        
        // 验证权限：只有作者可以查看详情
        if (!post.getAuthorId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权限查看点赞详情");
        }
        
        // 获取点赞列表
        List<LikeEntity> likes = likeRepository.findByPostId(postId);
        
        // 获取点赞总数
        Integer totalLikes = likeRepository.countByPostId(postId);
        
        // 获取用户信息
        List<Integer> userIds = likes.stream()
            .map(LikeEntity::getUserId)
            .distinct()
            .collect(Collectors.toList());
        
        List<UserEntity> users = userRepository.findAllById(userIds);
        
        // 转换为DTO
        List<LikedUserDTO> likedUsers = likes.stream()
            .map(like -> {
                UserEntity user = users.stream()
                    .filter(u -> u.getUserId().equals(like.getUserId()))
                    .findFirst()
                    .orElse(null);
                
                return new LikedUserDTO(
                    like.getUserId(),
                    user != null ? user.getUsername() : "未知用户",
                    user != null ? user.getAvatar() : "",
                    like.getCreatedAt()
                );
            })
            .collect(Collectors.toList());
        
        return new LikeDetailResponseDTO(totalLikes, likedUsers);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isPostLiked(Integer postId, Integer userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isCommentLiked(Integer commentId, Integer userId) {
        return likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent();
    }

    @Override
    @Transactional
    public Map<String, Object> toggleLike(LikeRequestDTO request, Integer userId) {
        
        // 验证目标是否存在
        validateTarget(request);
        
        // 检查是否已点赞
        Optional<LikeEntity> existingLike;
        
        if ("post".equals(request.getTargetType())) {
            existingLike = likeRepository.findByUserIdAndPostId(userId, request.getTargetId());
        } else if ("comment".equals(request.getTargetType())) {
            existingLike = likeRepository.findByUserIdAndCommentId(userId, request.getTargetId());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不支持的点赞类型");
        }
        
        Map<String, Object> result = new HashMap<>();
        
        if (existingLike.isPresent()) {
            // 取消点赞
            likeRepository.delete(existingLike.get());
            result.put("isLiked", false);
        } else {
            // 点赞
            LikeEntity like = new LikeEntity();
            like.setUserId(userId);
            like.setTargetType(request.getTargetType());
            like.setTargetId(request.getTargetId());
            like.setPostId("post".equals(request.getTargetType()) ? request.getTargetId() : request.getPostId());
            like.setCommentId("comment".equals(request.getTargetType()) ? request.getTargetId() : null);
            like.setCreatedAt(LocalDateTime.now());
            
            likeRepository.save(like);
            result.put("isLiked", true);
        }
        
        // 获取点赞数量
        int likeCount;
        if ("post".equals(request.getTargetType())) {
            likeCount = likeRepository.countByPostId(request.getTargetId());
            // 更新帖子的点赞数
            updatePostLikeCount(request.getTargetId(), likeCount);
        } else {
            likeCount = likeRepository.countByCommentId(request.getTargetId());
            // 更新评论的点赞数
            updateCommentLikeCount(request.getTargetId(), likeCount);
        }
        result.put("likeCount", likeCount);
        
        // 获取点赞时间
        result.put("likeTime", LocalDateTime.now());
        
        // 获取点赞用户信息
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            Map<String, Object> likedUser = new HashMap<>();
            likedUser.put("avatar", user.getAvatar());
            likedUser.put("username", user.getUsername());
            result.put("likedUser", likedUser);
        }
        
        return result;
    }
    
    private void validateTarget(LikeRequestDTO request) {
        if ("post".equals(request.getTargetType())) {
            // 验证帖子存在
            if (!postRepository.existsById(request.getTargetId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "帖子不存在");
            }
        } else if ("comment".equals(request.getTargetType())) {
            // 验证评论存在
            if (!commentRepository.existsById(request.getTargetId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "评论不存在");
            }
            
            // 如果有postId，验证评论属于该帖子
            if (request.getPostId() != null) {
                Optional<CommentEntity> commentOpt = commentRepository.findById(request.getTargetId());
                commentOpt.ifPresent(comment -> {
                    if (!comment.getPostId().equals(request.getPostId())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "评论不属于指定帖子");
                    }
                });
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不支持的点赞类型");
        }
    }
    
    private void updatePostLikeCount(Integer postId, int likeCount) {
        Optional<PostEntity> postOpt = postRepository.findById(postId);
        postOpt.ifPresent(post -> {
            post.setLikesCount(likeCount);
            postRepository.save(post);
        });
    }
    
    private void updateCommentLikeCount(Integer commentId, int likeCount) {
        Optional<CommentEntity> commentOpt = commentRepository.findById(commentId);
        commentOpt.ifPresent(comment -> {
            comment.setLikesCount(likeCount);
            commentRepository.save(comment);
        });
    }
}