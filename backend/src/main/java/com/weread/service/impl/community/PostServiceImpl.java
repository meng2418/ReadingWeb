package com.weread.service.impl.community;

import com.weread.dto.community.PostCreationDTO;
import com.weread.vo.book.BookSummaryVO;
import com.weread.vo.community.PostListVO;
import com.weread.entity.BookEntity;
import com.weread.entity.community.PostEntity;
import com.weread.entity.user.UserEntity;
import com.weread.entity.user.FollowEntity;
import com.weread.repository.BookRepository;
import com.weread.repository.community.LikeRepository;
import com.weread.repository.user.UserRepository;
import com.weread.repository.community.PostRepository;
import com.weread.repository.user.FollowRepository;
import com.weread.service.book.BookService;
import com.weread.service.community.PostService;
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

    public PostServiceImpl(BookService bookService, PostRepository postRepository,
            FollowRepository followRepository, BookRepository bookRepository,
            LikeRepository likeRepository, UserRepository userRepository) {
        this.bookService = bookService;
        this.postRepository = postRepository;
        this.followRepository = followRepository;
        this.bookRepository = bookRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
    }

    // ========================================================
    // 辅助方法：Entity -> VO
    // ========================================================
    private PostVO convertToPostVO(PostEntity entity, boolean isDetail) {
        PostVO vo = new PostVO();
        vo.setPostId(entity.getPostId());
        vo.setAuthorId(entity.getAuthorId());
        vo.setTitle(entity.getTitle());
        vo.setLikesCount(entity.getLikesCount());
        vo.setCommentsCount(entity.getCommentsCount());
        vo.setCreatedAt(entity.getCreatedAt());

        // Top Likers
        int limit = 3;
        Pageable pageable = PageRequest.of(0, limit);

        // 适配 LikeEntity
        List<Integer> topLikerIds = likeRepository.findTopNUserIdsByPostId(entity.getPostId(), pageable)
                .stream()
                .map(Math::toIntExact)
                .toList();

        if (!topLikerIds.isEmpty()) {
            List<UserEntity> userEntities = userRepository.findAllById(topLikerIds);
            List<UserSummaryVO> topLikers = userEntities.stream()
                    .map(this::convertToUserSummaryVO)
                    .toList();
            vo.setTopLikers(topLikers);
        } else {
            vo.setTopLikers(Collections.emptyList());
        }

        // 内容摘要
        if (entity.getContent() != null) {
            vo.setContentSummary(isDetail
                    ? entity.getContent()
                    : entity.getContent().substring(0, Math.min(entity.getContent().length(), 150)) + "...");
        } else {
            vo.setContentSummary("");
        }

        // 相关书本
        if (entity.getRelatedBooks() != null && !entity.getRelatedBooks().isEmpty()) {
            List<BookSummaryVO> bookSummaries = entity.getRelatedBooks().stream()
                    .map(bookService::convertToSummaryVO)
                    .toList();
            vo.setRelatedBooks(bookSummaries);
        } else {
            vo.setRelatedBooks(Collections.emptyList());
        }

        // 标签
        if (entity.getRelatedTopics() != null) {
            vo.setHashtags(entity.getRelatedTopics());
        } else {
            vo.setHashtags(Collections.emptyList());
        }

        return vo;
    }

    private UserSummaryVO convertToUserSummaryVO(UserEntity userEntity) {
        UserSummaryVO vo = new UserSummaryVO();
        vo.setUserId(userEntity.getUserId());
        vo.setUsername(userEntity.getUsername());
        vo.setAvatarUrl(userEntity.getAvatar());
        return vo;
    }

    // ========================================================
    // 1. 发帖
    // ========================================================
    @Override
    @Transactional
    public PostVO createPost(PostCreationDTO dto, Integer authorId) {
        PostEntity entity = new PostEntity();
        entity.setAuthorId(authorId);
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());

        // 处理 Books
        if (dto.getBookIds() != null && !dto.getBookIds().isEmpty()) {
            List<Integer> bookIds = dto.getBookIds();
            List<BookEntity> books = bookRepository.findAllById(bookIds);
            entity.setRelatedBooks(books);
        } else {
            entity.setRelatedBooks(Collections.emptyList());
        }

        // 处理 Topics
        if (dto.getTopics() != null && !dto.getTopics().isEmpty()) {
            entity.setRelatedTopics(dto.getTopics());
        } else {
            entity.setRelatedTopics(Collections.emptyList());
        }

        entity = postRepository.save(entity);
        return convertToPostVO(entity, true);
    }

    // ========================================================
    // 2. 获取单个帖子
    // ========================================================
    @Override
    public PostVO getPostById(Integer postId) {
        PostEntity entity = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "帖子不存在"));

        if (entity.getStatus() != 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "该帖子已被删除或正在审核");
        }

        return convertToPostVO(entity, true);
    }

    // ========================================================
    // 3. 获取帖子列表
    // ========================================================
    @Override
    public PostListVO getPostList(int page, int limit, String type,
            List<String> topics, Integer currentUserId) {

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        Specification<PostEntity> spec = Specification.where((root, query, cb) -> cb.equal(root.get("status"), 0));

        // 用户类型筛选
        if ("mine".equals(type)) {
            if (currentUserId == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请登录以查看我的帖子");
            spec = spec.and((root, query, cb) -> cb.equal(root.get("authorId"), currentUserId));
        } else if ("following".equals(type)) {
            if (currentUserId == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请登录以查看关注的帖子");
            List<Integer> followedAuthorIds = this.getAllFollowedUserIds(currentUserId);
            if (followedAuthorIds.isEmpty())
                return new PostListVO(Collections.emptyList(), 0L, page, limit);
            spec = spec.and((root, query, cb) -> root.get("authorId").in(followedAuthorIds));
        }

        // 标签筛选
        if (topics != null && !topics.isEmpty()) {
            for (String topic : topics) {
                spec = spec.and((root, query, cb) -> cb.like(root.get("relatedTopics"), "%" + topic + "%"));
            }
        }

        Page<PostEntity> postPage = postRepository.findAll(spec, pageable);
        List<PostVO> postVOs = postPage.getContent().stream()
                .map(entity -> convertToPostVO(entity, false))
                .toList();

        return new PostListVO(postVOs, postPage.getTotalElements(), page, limit);
    }

    // ========================================================
    // 辅助方法：获取用户关注列表 ID
    // ========================================================
    private List<Integer> getAllFollowedUserIds(Integer currentUserId) {
        final int MAX_FOLLOWS_LIMIT = 5000;
        Pageable pageable = PageRequest.of(0, MAX_FOLLOWS_LIMIT);
        Page<FollowEntity> followPage = followRepository.findByFollowerId(currentUserId, pageable);
        return followPage.getContent().stream()
                .map(FollowEntity::getFollowingId)
                .toList();
    }
}
