package com.weread.service.impl.community;

import com.weread.dto.community.CommentCreationDTO;
import com.weread.vo.community.CommentListVO;
import com.weread.entity.community.CommentEntity;
import com.weread.entity.community.LikeEntity;
import com.weread.entity.user.UserEntity;
import com.weread.repository.community.CommentRepository;
import com.weread.repository.community.LikeRepository;
import com.weread.repository.community.PostRepository;
import com.weread.repository.user.UserRepository;
import com.weread.service.community.CommunityService;
import com.weread.vo.community.CommentVO;
import com.weread.vo.community.LikeInfoVO;
import com.weread.vo.user.UserSummaryVO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommunityServiceImpl implements CommunityService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    // --- 新增常量 ---
    private static final int DEFAULT_TOP_REPLY_LIMIT = 3;

    public CommunityServiceImpl(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository, LikeRepository likeRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
    }

    @Override
    @Transactional
    public CommentVO createComment(Integer postId, Integer userId, CommentCreationDTO dto) {
        // 验证帖子存在性
        postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));

        CommentEntity entity = new CommentEntity();
        entity.setPostId(postId);
        entity.setUserId(userId);
        entity.setContent(dto.getContent());
        entity.setParentCommentId(dto.getParentCommentId());

        entity = commentRepository.save(entity);

        // 更新 PostEntity/ParentComment 的 commentsCount 计数
        postRepository.findById(postId).ifPresent(post -> {
            post.setCommentsCount(post.getCommentsCount() + 1);
            postRepository.save(post);
        });

        if (dto.getParentCommentId() != null) {
            commentRepository.findById(dto.getParentCommentId()).ifPresent(parentComment -> {
                parentComment.setReplyCount(parentComment.getReplyCount() + 1);
                commentRepository.save(parentComment);
            });
        }

        return convertToCommentVO(entity, userId);
    }

    @Override
    public CommentListVO getPostComments(Integer postId, int page, int limit, Integer currentUserId) {
        Pageable pageable = PageRequest.of(page - 1, limit);

        Page<CommentEntity> commentPage = commentRepository.findByPostIdAndParentCommentIdIsNullOrderByCreatedAtDesc(postId, pageable);

        List<CommentVO> commentVOs = commentPage.getContent().stream()
                .map(entity -> {
                    CommentVO vo = convertToCommentVO(entity, currentUserId);
                    vo.setReplyCount((int) commentRepository.countByParentCommentId(entity.getCommentId()));
                    if (vo.getReplyCount() > 0) {
                        vo.setReplies(getTopReplies(entity.getCommentId(), currentUserId, DEFAULT_TOP_REPLY_LIMIT));
                    }
                    return vo;
                }).toList();

        CommentListVO response = new CommentListVO();
        response.setComments(commentVOs);
        response.setTotalElements(commentPage.getTotalElements());
        response.setTotalPages(commentPage.getTotalPages());
        response.setCurrentPage(commentPage.getNumber() + 1);

        return response;
    }

    @Override
    public List<CommentVO> getCommentReplies(Integer parentCommentId, int page, int limit, Integer currentUserId) {
        commentRepository.findById(parentCommentId).orElseThrow(() -> new IllegalArgumentException("Parent comment not found with ID: " + parentCommentId));

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").ascending());
        List<CommentEntity> replyPage = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(parentCommentId, pageable);

        return replyPage.stream().map(entity -> convertToCommentVO(entity, currentUserId)).toList();
    }

    // --- 辅助方法 ---
    private CommentVO convertToCommentVO(CommentEntity entity, Integer currentUserId) {
        CommentVO vo = new CommentVO();
        vo.setCommentId(entity.getCommentId());
        vo.setPostId(entity.getPostId());
        vo.setParentCommentId(entity.getParentCommentId());
        vo.setContent(entity.getContent());
        vo.setLikesCount(entity.getLikesCount());
        vo.setCreatedAt(entity.getCreatedAt());

        Optional<UserEntity> userOpt = userRepository.findById(entity.getUserId());
        userOpt.ifPresent(user -> vo.setAuthor(convertToUserSummaryVO(user)));

        boolean isLiked = false;
        if (currentUserId != null) {
            // 使用新 LikeEntity 结构，点赞评论检查
            isLiked = likeRepository.findByCommentIdAndUserId(entity.getCommentId(), currentUserId).isPresent();
        }
        vo.setLiked(isLiked);

        return vo;
    }

    private List<CommentVO> getTopReplies(Integer parentCommentId, Integer currentUserId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<CommentEntity> replies = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(parentCommentId, pageable);
        return replies.stream().map(entity -> convertToCommentVO(entity, currentUserId)).toList();
    }

    private UserSummaryVO convertToUserSummaryVO(UserEntity userEntity) {
        UserSummaryVO vo = new UserSummaryVO();
        vo.setUserId(userEntity.getUserId());
        vo.setUsername(userEntity.getUsername());
        vo.setAvatarUrl(userEntity.getAvatar());
        return vo;
    }

    // --- 点赞逻辑，适配 LikeEntity ---
    @Override
    public LikeInfoVO getTargetLikes(String targetType, Integer targetId, int limit) {
        LikeInfoVO vo = new LikeInfoVO();
        vo.setLiked(false);

        int totalLikes = 0;
        List<Integer> topLikerIds = List.of();

        if ("POST".equals(targetType)) {
            totalLikes = likeRepository.countByPostId(targetId);
            Pageable pageable = PageRequest.of(0, limit);
            topLikerIds = likeRepository.findTopNUserIdsByPostId(targetId, pageable);
        } else if ("COMMENT".equals(targetType)) {
            totalLikes = likeRepository.countByCommentId(targetId);
            Pageable pageable = PageRequest.of(0, limit);
            topLikerIds = likeRepository.findTopNUserIdsByCommentId(targetId, pageable);
        }

        List<UserSummaryVO> likedUsers = topLikerIds.stream()
                .map(userRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::convertToUserSummaryVO)
                .toList();

        vo.setTotalLikes(totalLikes);
        vo.setLikedUsers(likedUsers);

        return vo;
    }

    @Override
    @Transactional
    public void likeTarget(String targetType, Integer targetId, Integer userId) {
        Optional<LikeEntity> existingLike = Optional.empty();
        if ("POST".equals(targetType)) {
            existingLike = likeRepository.findByPostIdAndUserId(targetId, userId);
        } else if ("COMMENT".equals(targetType)) {
            existingLike = likeRepository.findByCommentIdAndUserId(targetId, userId);
        }

        if (existingLike.isPresent()) return;

        boolean targetExists = updateLikeCount(targetType, targetId, 1);
        if (!targetExists) {
            throw new IllegalArgumentException(targetType + " ID:" + targetId + " 不存在。无法点赞。");
        }

        LikeEntity like = new LikeEntity();
        like.setUserId(userId);
        if ("POST".equals(targetType)) like.setPostId(targetId);
        else if ("COMMENT".equals(targetType)) like.setCommentId(targetId);

        likeRepository.save(like);
    }

    @Override
    @Transactional
    public void unlikeTarget(String targetType, Integer targetId, Integer userId) {
        Optional<LikeEntity> existingLike = Optional.empty();
        if ("POST".equals(targetType)) existingLike = likeRepository.findByPostIdAndUserId(targetId, userId);
        else if ("COMMENT".equals(targetType)) existingLike = likeRepository.findByCommentIdAndUserId(targetId, userId);

        if (existingLike.isEmpty()) return;

        likeRepository.delete(existingLike.get());
        updateLikeCount(targetType, targetId, -1);
    }

    private boolean updateLikeCount(String targetType, Integer targetId, int change) {
        if ("POST".equals(targetType)) {
            return postRepository.findById(targetId).map(post -> {
                post.setLikesCount(post.getLikesCount() + change);
                postRepository.save(post);
                return true;
            }).orElse(false);
        } else if ("COMMENT".equals(targetType)) {
            return commentRepository.findById(targetId).map(comment -> {
                comment.setLikesCount(comment.getLikesCount() + change);
                commentRepository.save(comment);
                return true;
            }).orElse(false);
        }
        return false;
    }
}
