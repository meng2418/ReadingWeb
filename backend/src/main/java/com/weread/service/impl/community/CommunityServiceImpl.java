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
    private static final String TARGET_TYPE_POST = "POST";
    private static final String TARGET_TYPE_COMMENT = "COMMENT";
    private static final int DEFAULT_TOP_REPLY_LIMIT = 3; 

    public CommunityServiceImpl(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository, LikeRepository likeRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
    }

    @Override
    @Transactional
    public CommentVO createComment(Long postId, Long userId, CommentCreationDTO dto) {
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
        
        // A. 更新帖子总评论计数
        postRepository.findById(postId).ifPresent(post -> {
            // 假设 PostEntity 具有 setCommentsCount 方法
            post.setCommentsCount(post.getCommentsCount() + 1); 
            postRepository.save(post);
        });
        
        // B. 如果是回复，更新父评论的回复计数
        if (dto.getParentCommentId() != null) {
            commentRepository.findById(dto.getParentCommentId()).ifPresent(parentComment -> {
                // 假设 CommentEntity 具有 setReplyCount 方法
                parentComment.setReplyCount(parentComment.getReplyCount() + 1); 
                commentRepository.save(parentComment);
            });
        }
        
        return convertToCommentVO(entity, userId); 
    }

    @Override
    public CommentListVO getPostComments(Long postId, int page, int limit, Long currentUserId) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        
        // 仅查询顶级评论 (ParentCommentIdIsNull)
        Page<CommentEntity> commentPage = commentRepository.findByPostIdAndParentCommentIdIsNullOrderByCreatedAtDesc(postId, pageable);
        
        List<CommentVO> commentVOs = commentPage.getContent().stream()
            .map(entity -> {
                CommentVO vo = convertToCommentVO(entity, currentUserId);
                
                // 获取回复总数
                vo.setReplyCount((int) commentRepository.countByParentCommentId(entity.getCommentId())); 
                
                // 如果需要，可以加载前 N 条回复到 vo.setReplies()
                if (vo.getReplyCount() > 0) {
                    vo.setReplies(getTopReplies(entity.getCommentId(), currentUserId, DEFAULT_TOP_REPLY_LIMIT));
                }
                
                return vo;
            })
            .toList();

        CommentListVO response = new CommentListVO();
        response.setComments(commentVOs);
        response.setTotalElements(commentPage.getTotalElements());
        response.setTotalPages(commentPage.getTotalPages());
        response.setCurrentPage(commentPage.getNumber() + 1);
        
        return response;
    }

    @Override
    public List<CommentVO> getCommentReplies(Long parentCommentId, int page, int limit, Long currentUserId) {
        // 验证父评论存在性
        commentRepository.findById(parentCommentId).orElseThrow(() -> new IllegalArgumentException("Parent comment not found with ID: " + parentCommentId));
    
        //创建 Pageable 变量】
        // Spring Data JPA 的页码从 0 开始 (page - 1)
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").ascending()); // 明确指定排序，与 Repository 方法名保持一致
        //调用 Repository 方法并传入 pageable】
        // Repository 方法应返回 Page<CommentEntity>
        List<CommentEntity> replyPage = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(parentCommentId, pageable);
    
        List<CommentEntity> replies = replyPage; // 获取实际的 List
    
        return replies.stream().map(entity -> convertToCommentVO(entity, currentUserId)).toList();
    }

    // --- 辅助方法：Entity 到 VO 的转换 ---
    private CommentVO convertToCommentVO(CommentEntity entity, Long currentUserId) {
        CommentVO vo = new CommentVO();
        vo.setCommentId(entity.getCommentId());
        vo.setPostId(entity.getPostId());
        vo.setParentCommentId(entity.getParentCommentId());
        vo.setContent(entity.getContent());
        vo.setLikesCount(entity.getLikesCount());
        vo.setCreatedAt(entity.getCreatedAt());

        // 【设置评论者信息】
        Optional<UserEntity> userOpt = userRepository.findById(entity.getUserId());
    
        userOpt.ifPresent(user -> 
            vo.setAuthor(convertToUserSummaryVO(user))
        );

        // 【检查当前用户是否点赞】
        boolean isLiked = false;
        if (currentUserId != null) {
            isLiked = likeRepository.findByTargetTypeAndTargetIdAndUserId(
                TARGET_TYPE_COMMENT, entity.getCommentId(), currentUserId).isPresent();
        } 
        vo.setLiked(isLiked); // 使用正确的 setLiked() 方法
        
        return vo;
    }
    
    // --- 新增辅助方法：获取前 N 条回复 ---
    private List<CommentVO> getTopReplies(Long parentCommentId, Long currentUserId, int limit) {
        // 假设 findByParentCommentIdOrderByCreatedAtAsc 支持 Pageable
        Pageable pageable = PageRequest.of(0, limit);
        
        // 假设 findByParentCommentIdOrderByCreatedAtAsc(Long parentId, Pageable pageable) 存在
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

    @Override
    public LikeInfoVO getTargetLikes(String targetType, Long targetId, int limit) {
        LikeInfoVO vo = new LikeInfoVO();
        vo.setLiked(false);
        
        long totalLikes = likeRepository.countByTargetTypeAndTargetId(targetType, targetId); 
        vo.setTotalLikes(totalLikes);

        Pageable pageable = PageRequest.of(0, limit);
        
        List<Long> topLikerIds = likeRepository.findTopNUserIdsByTarget(
            targetType, 
            targetId, 
            pageable); 
        
        List<UserSummaryVO> likedUsers = topLikerIds.stream().map(userRepository::findById).filter(Optional::isPresent).map(Optional::get).map(this::convertToUserSummaryVO).toList();
            
        vo.setLikedUsers(likedUsers);

        return vo;
    }
    
    @Override
    @Transactional
    public void likeTarget(String targetType, Long targetId, Long userId) {
        Optional<LikeEntity> existingLike = likeRepository.findByTargetTypeAndTargetIdAndUserId(targetType, targetId, userId);
        if (existingLike.isPresent()) {
            return; 
        }

        boolean targetExists = updateLikeCount(targetType, targetId, 1);
        
        if (!targetExists) {
            throw new IllegalArgumentException(targetType + " ID:" + targetId + " 不存在。无法点赞。");
        }

        LikeEntity like = new LikeEntity();
        like.setTargetType(targetType);
        like.setTargetId(targetId);
        like.setUserId(userId);
        likeRepository.save(like);
    }

    @Override
    @Transactional
    public void unlikeTarget(String targetType, Long targetId, Long userId) {
        Optional<LikeEntity> existingLike = likeRepository.findByTargetTypeAndTargetIdAndUserId(targetType, targetId, userId);
        if (existingLike.isEmpty()) {
            return;
        }

        likeRepository.delete(existingLike.get());

        updateLikeCount(targetType, targetId, -1);
    }

    /**
     * 辅助方法：更新 PostEntity 或 CommentEntity 的点赞数
     */
    private boolean updateLikeCount(String targetType, Long targetId, int change) {
        if (TARGET_TYPE_POST.equals(targetType)) {
            return postRepository.findById(targetId).map(post -> {
                post.setLikesCount(post.getLikesCount() + change);
                postRepository.save(post);
                return true;
            }).orElse(false);
        } else if (TARGET_TYPE_COMMENT.equals(targetType)) {
            return commentRepository.findById(targetId).map(comment -> {
                comment.setLikesCount(comment.getLikesCount() + change);
                commentRepository.save(comment);
                return true;
            }).orElse(false);
        }
        return false;
    }

}