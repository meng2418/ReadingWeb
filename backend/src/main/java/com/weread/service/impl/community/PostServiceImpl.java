package com.weread.service.impl.community;

import com.weread.dto.community.PostCreationDTO;
import com.weread.vo.book.BookSummaryVO;
import com.weread.vo.community.PostListVO;
import com.weread.entity.BookEntity;
import com.weread.entity.community.PostEntity;
import com.weread.entity.user.UserEntity;
import com.weread.entity.user.FollowEntity; // 【重要】需要引入 FollowEntity
import com.weread.repository.BookRepository;
import com.weread.repository.community.LikeRepository;
import com.weread.repository.user.UserRepository;
import com.weread.repository.community.PostRepository;
import com.weread.repository.user.FollowRepository;
import com.weread.service.book.BookService;
import com.weread.service.community.PostService;
import com.weread.service.community.CommunityService;
import com.weread.vo.community.PostVO;
import com.weread.vo.user.UserSummaryVO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final BookService bookService;

    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final BookRepository bookRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    
    public PostServiceImpl(BookService bookService, PostRepository postRepository, FollowRepository followRepository, BookRepository bookRepository, LikeRepository likeRepository, UserRepository userRepository) {
        this.bookService = bookService;
        this.postRepository = postRepository;
        this.followRepository = followRepository;
        this.bookRepository = bookRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
    }
    
    // ------------------------------------------------------------------
    // 辅助方法：转换 Entity -> VO
    // ------------------------------------------------------------------
    private PostVO convertToPostVO(PostEntity entity, boolean isDetail) {
        PostVO vo = new PostVO();
        vo.setPostId(entity.getPostId());
        vo.setAuthorId(entity.getAuthorId());
        vo.setTitle(entity.getTitle());
        vo.setLikesCount(entity.getLikesCount());
        vo.setCommentsCount(entity.getCommentsCount());
        vo.setCreatedAt(entity.getCreatedAt());
        
        // =========================================================
        // 【逻辑：获取 Top Likers】
        // =========================================================
        int limit = 3; 
        Pageable pageable = PageRequest.of(0, limit);
        
        // 假设 LikeRepository 中 findTopNUserIdsByTarget 已定义
        List<Long> topLikerIds = likeRepository.findTopNUserIdsByTarget(
            CommunityService.TARGET_TYPE_POST, entity.getPostId(), pageable); 
        
        if (!topLikerIds.isEmpty()) {
            // userRepository.findAllById 接受 Iterable<ID>
            List<UserEntity> userEntities = userRepository.findAllById(topLikerIds); 
            
            // 转换为 UserSummaryVO
            List<UserSummaryVO> topLikers = userEntities.stream().map(this::convertToUserSummaryVO).toList();
                
            vo.setTopLikers(topLikers);
        } else {
            vo.setTopLikers(Collections.emptyList());
        }

        // 列表页只展示摘要，详情页展示全部
        if (entity.getContent() != null) {
            vo.setContentSummary(isDetail ? entity.getContent() : entity.getContent().substring(0, Math.min(entity.getContent().length(), 150)) + "...");
        } else {
             vo.setContentSummary("");
        }
        
        if (entity.getRelatedBooks() != null && !entity.getRelatedBooks().isEmpty()) {
            List<BookSummaryVO> bookSummaries = entity.getRelatedBooks().stream()
            .map(bookEntity -> {
                BookSummaryVO summaryvo = bookService.convertToSummaryVO(bookEntity); 
        
            // 【✅ 确保使用 return 关键字】
            return summaryvo; 
            })
            .toList();
        
            vo.setRelatedBooks(bookSummaries);
        } else {
            vo.setRelatedBooks(Collections.emptyList());
        }
        
        if (entity.getHashtags() != null) {
             vo.setHashtags(entity.getHashtags());
        } else {
             vo.setHashtags(Collections.emptyList());
        }
        
        return vo;
    }

    /**
     * 【辅助方法】：将 UserEntity 转换为 UserSummaryVO (已修正)
     */
    private UserSummaryVO convertToUserSummaryVO(UserEntity userEntity) {
        UserSummaryVO vo = new UserSummaryVO();
        
        // 假设 UserEntity 的 ID 字段是 getId()
        vo.setUserId(userEntity.getUserId());
        vo.setUsername(userEntity.getUsername());
        
        // 假设 UserEntity 的头像字段是 getAvatar()
        vo.setAvatarUrl(userEntity.getAvatar()); 

        return vo;
    }


    // ------------------------------------------------------------------
    // 1. 发帖
    // ------------------------------------------------------------------
    @Override
    @Transactional
    public PostVO createPost(PostCreationDTO dto, Long authorId) {
        PostEntity entity = new PostEntity();
        entity.setAuthorId(authorId);
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        
        // 处理 Books
        if (dto.getBookIds() != null && !dto.getBookIds().isEmpty()) {
    
            List<Integer> bookIdIntegers = dto.getBookIds().stream().map(Long::intValue).toList();

            List<BookEntity> books = bookRepository.findAllById(bookIdIntegers); 
            
            entity.setRelatedBooks(books);
        } else {
            entity.setRelatedBooks(Collections.emptyList());
        }

        // 设置 hashtags 
        entity.setHashtags(dto.getHashtags() != null ? dto.getHashtags() : Collections.emptyList());
        
        entity = postRepository.save(entity);
        return convertToPostVO(entity, true);
    }

    // ------------------------------------------------------------------
    // 2. 获取单个帖子
    // ------------------------------------------------------------------
    @Override
    public PostVO getPostById(Long postId) {
        PostEntity entity = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "帖子不存在"));
        
        if (entity.getStatus() != 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "该帖子已被删除或正在审核");
        }
        
        return convertToPostVO(entity, true);
    }

    // ------------------------------------------------------------------
    // 3. 获取帖子列表
    // ------------------------------------------------------------------
    @Override
    public PostListVO getPostList(int page, int limit, String type, 
                                     List<String> hashtags, Long currentUserId) {

        // 基础排序和分页
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        
        // 动态查询条件
        Specification<PostEntity> spec = Specification.where((root, query, cb) -> cb.equal(root.get("status"), 0)); // 只看正常状态的帖子
        
        // A. 用户类型筛选
        if ("mine".equals(type)) {
            if (currentUserId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请登录以查看我的帖子");
            spec = spec.and((root, query, cb) -> cb.equal(root.get("authorId"), currentUserId));
        } else if ("following".equals(type)) {
            if (currentUserId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请登录以查看关注的帖子");
            
            // 【✅ 修正点：调用辅助方法获取关注的用户 ID 列表】
            List<Long> followedAuthorIds = this.getAllFollowedUserIds(currentUserId);
            
            if (followedAuthorIds.isEmpty()) {
                // 如果没有关注任何人，直接返回空列表
                return new PostListVO(Collections.emptyList(), 0L, page, limit);
            }
            
            // 使用正确的 authorId 字段名
            spec = spec.and((root, query, cb) -> root.get("authorId").in(followedAuthorIds));
        }
        // else "all" (默认是所有正常帖子)

        // B. 标签筛选
        if (hashtags != null && !hashtags.isEmpty()) {
            // 实现 AND 逻辑：帖子必须包含所有指定的标签
            for (String tag : hashtags) {
                // 使用 LIKE 查询包含特定标签的 JSON/逗号分隔字符串
                spec = spec.and((root, query, cb) -> cb.like(root.get("hashtags"), "%" + tag + "%"));
            }
        }
        
        // 执行查询
        Page<PostEntity> postPage = postRepository.findAll(spec, pageable);
        
        // 转换结果
        List<PostVO> postVOs = postPage.getContent().stream().map(entity -> convertToPostVO(entity, false)).toList();
        
        return new PostListVO(postVOs, postPage.getTotalElements(), page, limit);
    }

    /**
     * 【新增辅助方法】: 获取当前用户关注的所有用户的 ID 列表。
     * 使用现有的 findByFollowerId(Long followerId, Pageable pageable) 方法。
     */
    private List<Long> getAllFollowedUserIds(Long currentUserId) {
        // 使用一个较大的限制来获取所有关注列表 (变通方案)
        final int MAX_FOLLOWS_LIMIT = 5000; 
        Pageable pageable = PageRequest.of(0, MAX_FOLLOWS_LIMIT);
        
        // 调用 FollowRepository 中已有的方法
        Page<FollowEntity> followPage = followRepository.findByFollowerId(currentUserId, pageable);
        
        // 提取 FollowingId (即被关注者的 ID，也就是帖子作者 ID)
        return followPage.getContent().stream().map(FollowEntity::getFollowingId).toList();
    }
}