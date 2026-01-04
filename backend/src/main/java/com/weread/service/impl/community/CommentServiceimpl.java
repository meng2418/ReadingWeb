package com.weread.service.impl.community;

import com.weread.dto.community.*;
import com.weread.entity.community.CommentEntity;
import com.weread.entity.community.PostEntity;
import com.weread.entity.user.UserEntity;
import com.weread.repository.community.CommentRepository;
import com.weread.repository.community.PostRepository;
import com.weread.repository.user.UserRepository;
import com.weread.service.community.CommentService;
import com.weread.service.community.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceimpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeService likeService;

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDTO> getCommentsByPostId(Integer postId, int page, int limit, Integer currentUserId) {
        // 验证帖子存在
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "帖子不存在");
        }
        
        // 创建分页
        if (page < 1) page = 1;
        if (limit < 1) limit = 20;
        if (limit > 100) limit = 100;
        
        Pageable pageable = PageRequest.of(page - 1, limit);
        
        // 获取一级评论
        Page<CommentEntity> commentPage = commentRepository.findByPostIdAndParentCommentIdIsNullOrderByCreatedAtDesc(postId, pageable);
        
        // 转换为DTO
        List<CommentDTO> commentDTOs = commentPage.getContent().stream()
            .map(comment -> convertToCommentDTO(comment, currentUserId))
            .collect(Collectors.toList());
        
        // 获取每个评论的回复（前3条）
        for (CommentDTO commentDTO : commentDTOs) {
            Pageable replyPageable = PageRequest.of(0, 3);
            List<CommentEntity> replies = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(
                commentDTO.getCommentId(), replyPageable);
            
            List<CommentDTO> replyDTOs = replies.stream()
                .map(reply -> convertToCommentDTO(reply, currentUserId))
                .collect(Collectors.toList());
            
            commentDTO.setReplies(replyDTOs);
        }
        
        return new PageImpl<>(commentDTOs, pageable, commentPage.getTotalElements());
    }

    @Override
    @Transactional
    public CommentResponseDTO createComment(Integer postId, CreateCommentRequestDTO request, Integer userId) {
        // 验证帖子存在
        PostEntity post = postRepository.findById(postId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "帖子不存在"));
        
        // 创建评论
        CommentEntity comment = new CommentEntity();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setParentCommentId(request.getParentId());
        
        CommentEntity savedComment = commentRepository.save(comment);
        
        // 更新帖子评论数
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);
        
        // 如果是对评论的回复，更新父评论的回复数
        if (request.getParentId() != null) {
            commentRepository.findById(request.getParentId()).ifPresent(parent -> {
                parent.setReplyCount(parent.getReplyCount() + 1);
                commentRepository.save(parent);
            });
        }
        
        // 转换为DTO
        CommentDTO commentDTO = convertToCommentDTO(savedComment, userId);
        
        return new CommentResponseDTO(commentDTO, post.getCommentsCount());
    }

    @Override
    @Transactional
    public ReplyResponseDTO replyComment(Integer commentId, CreateCommentRequestDTO request, Integer userId) {
        // 验证父评论存在
        CommentEntity parentComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "评论不存在"));
        
        // 不能回复自己
        if (parentComment.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不能回复自己的评论");
        }
        
        // 创建回复
        CommentEntity reply = new CommentEntity();
        reply.setPostId(parentComment.getPostId());
        reply.setUserId(userId);
        reply.setContent(request.getContent());
        reply.setParentCommentId(commentId);
        
        CommentEntity savedReply = commentRepository.save(reply);
        
        // 更新父评论的回复数
        parentComment.setReplyCount(parentComment.getReplyCount() + 1);
        commentRepository.save(parentComment);
        
        // 转换为DTO
        CommentDTO replyDTO = convertToCommentDTO(savedReply, userId);
        
        // 设置被回复的用户名
        UserEntity parentUser = userRepository.findById(parentComment.getUserId()).orElse(null);
        if (parentUser != null) {
            replyDTO.setReplyToUsername(parentUser.getUsername());
        }
        
        return new ReplyResponseDTO(replyDTO, parentComment.getReplyCount());
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDTO getCommentById(Integer commentId, Integer currentUserId) {
        CommentEntity comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "评论不存在"));
        
        return convertToCommentDTO(comment, currentUserId);
    }

    private CommentDTO convertToCommentDTO(CommentEntity comment, Integer currentUserId) {
        CommentDTO dto = new CommentDTO();
        dto.setCommentId(comment.getCommentId());
        dto.setUserId(comment.getUserId());
        dto.setContent(comment.getContent());
        dto.setCommentTime(comment.getCreatedAt());
        dto.setParentCommentId(comment.getParentCommentId());
        dto.setLikeCount(comment.getLikesCount());
        
        // 获取用户信息
        UserEntity user = userRepository.findById(comment.getUserId()).orElse(null);
        if (user != null) {
            dto.setUsername(user.getUsername());
            dto.setAvatar(user.getAvatar());
        }
        
        // 检查是否点赞
        if (currentUserId != null) {
            dto.setIsLiked(likeService.isCommentLiked(comment.getCommentId(), currentUserId));
        } else {
            dto.setIsLiked(false);
        }
        
        return dto;
    }    
}
