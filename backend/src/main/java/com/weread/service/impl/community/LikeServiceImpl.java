package com.weread.service.impl.community;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.weread.dto.community.LikeDetailResponseDTO;
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
}