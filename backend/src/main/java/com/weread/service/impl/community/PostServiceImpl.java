package com.weread.service.impl.community;

import com.weread.dto.community.AuthorInfoDTO;
import com.weread.dto.community.BookSearchResponseDTO;
import com.weread.dto.community.BookSearchResultDTO;
import com.weread.dto.community.BookSimpleDTO;
import com.weread.dto.community.PostCreationDTO;
import com.weread.dto.community.PostSquareDTO;
import com.weread.dto.community.RelatedBookDTO;
import com.weread.dto.community.TopicSearchResponseDTO;
import com.weread.dto.community.TopicSearchResultDTO;
import com.weread.dto.community.UserPostsResponseDTO;
import com.weread.vo.book.AuthorVO;
import com.weread.vo.book.MentionedBookVO;
import com.weread.vo.community.PostListVO;
import com.weread.entity.book.BookEntity;
import com.weread.entity.community.PostEntity;
import com.weread.entity.community.TopicEntity;
import com.weread.entity.user.FollowEntity;
import com.weread.entity.user.UserEntity;
import com.weread.repository.book.BookRepository;
import com.weread.repository.community.LikeRepository;
import com.weread.repository.user.UserRepository;
import com.weread.repository.community.PostRepository;
import com.weread.repository.community.TopicFollowRepository;
import com.weread.repository.user.FollowRepository;
import com.weread.repository.community.TopicRepository;
import com.weread.service.book.BookService;
import com.weread.service.community.PostService;
import com.weread.service.user.FollowService;
import com.weread.util.ImagePathUtils;
import com.weread.service.community.LikeService;
import com.weread.vo.community.PostVO;
import com.weread.vo.community.TopicPostVO;
import com.weread.vo.community.TopicPostRelatedVO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final BookService bookService;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final TopicFollowRepository topicFollowRepository;
    private final BookRepository bookRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final FollowService followService;
    private final LikeService likeService;

    public PostServiceImpl(BookService bookService, PostRepository postRepository,
            FollowRepository followRepository, TopicFollowRepository topicFollowRepository,
            BookRepository bookRepository,
            LikeRepository likeRepository, UserRepository userRepository,
            TopicRepository topicRepository, FollowService followService, LikeService likeService) {
        this.bookService = bookService;
        this.postRepository = postRepository;
        this.followRepository = followRepository;
        this.topicFollowRepository = topicFollowRepository;
        this.bookRepository = bookRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.followService = followService;
        this.likeService = likeService;
    }

    // ========================================================
    // 新增方法：获取话题下的帖子列表
    // ========================================================
    @Override
    @Transactional(readOnly = true)
    public List<TopicPostVO> getTopicPosts(Integer topicId, String sort, Integer cursor, Integer limit,
            Integer currentUserId) {

        // 验证话题是否存在
        if (!topicRepository.existsById(topicId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "话题不存在");
        }

        // 参数验证和默认值
        if (limit == null || limit <= 0)
            limit = 20;
        if (limit > 100)
            limit = 100;

        // 验证sort参数
        if (!List.of("latest", "hot", "quality").contains(sort)) {
            sort = "latest";
        }

        List<PostEntity> posts;

        if (cursor != null && cursor > 0) {
            // cursor-based 查询：postId > cursor
            Pageable pageable = PageRequest.of(0, limit);
            posts = postRepository.findPostsByTopicAndCursor(topicId, sort, cursor, limit);
        } else {
            // 第一页：查询最新的limit条
            posts = postRepository.findPostsByTopicAndSort(topicId, sort, limit);
        }

        if (posts.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量查询用户信息以提高性能
        List<Integer> authorIds = posts.stream()
                .map(PostEntity::getAuthorId)
                .distinct()
                .collect(Collectors.toList());

        List<UserEntity> authors = userRepository.findAllById(authorIds);

        // 转换
        return posts.stream()
                .map(post -> convertToTopicPostVO(post, authors, currentUserId, topicId))
                .collect(Collectors.toList());
    }

    private TopicPostVO convertToTopicPostVO(PostEntity post, List<UserEntity> authors, Integer currentUserId,
            Integer currentTopicId) {
        TopicPostVO vo = new TopicPostVO();

        // 基本字段
        vo.setPostId(post.getPostId());
        vo.setPostTitle(post.getTitle());
        vo.setContent(post.getContent());
        vo.setPublishTime(post.getCreatedAt());
        vo.setPublishLocation(""); // 如果PostEntity没有location字段，先设为空

        // 评论数和点赞数（需要PostEntity有这些字段）
        vo.setCommentCount(post.getCommentsCount());
        vo.setLikeCount(post.getLikesCount());

        // 作者信息
        UserEntity author = authors.stream()
                .filter(a -> a.getUserId().equals(post.getAuthorId()))
                .findFirst()
                .orElse(null);

        if (author != null) {
            AuthorVO authorVO = new AuthorVO();
            authorVO.setAuthorAvatar(author.getAvatar());
            authorVO.setAuthorName(author.getUsername());
            vo.setAuthor(authorVO);

            // 是否已关注作者
            if (currentUserId != null) {
                boolean isFollowing = followRepository.findByFollowerIdAndFollowingId(currentUserId, author.getUserId())
                        .isPresent();
                vo.setIsFollowingAuthor(isFollowing);
            } else {
                vo.setIsFollowingAuthor(false);
            }
        }

        // 是否已点赞（需要LikeService的实现）
        if (currentUserId != null && likeService != null) {
            try {
                vo.setIsLiked(likeService.isPostLiked(currentUserId, post.getPostId()));
            } catch (Exception e) {
                vo.setIsLiked(false); // 出错时默认为false
            }
        } else {
            vo.setIsLiked(false);
        }

        // 帖子的话题列表（需要PostEntity有postTopics关联）
        try {
            // 假设PostEntity有getPostTopics方法
            vo.setTopics(Collections.emptyList()); // 先设为空列表
        } catch (Exception e) {
            vo.setTopics(Collections.emptyList());
        }

        // 提到的书籍（需要PostEntity有mentionedBooks关联）
        try {
            vo.setMentionedBooks(Collections.emptyList()); // 先设为空列表
        } catch (Exception e) {
            vo.setMentionedBooks(Collections.emptyList());
        }

        // 相关话题（从数据库中查询相关话题）
        try {
            List<TopicEntity> relatedTopics = topicRepository.findRelatedTopicsByPostId(post.getPostId(),
                    currentTopicId,
                    org.springframework.data.domain.PageRequest.of(0, 5));
            List<TopicPostRelatedVO> relatedTopicVOs = relatedTopics.stream()
                    .map(this::convertToTopicPostRelatedVO)
                    .collect(Collectors.toList());
            vo.setRelatedTopics(relatedTopicVOs);
        } catch (Exception e) {
            vo.setRelatedTopics(Collections.emptyList());
        }

        return vo;
    }

    private TopicPostRelatedVO convertToTopicPostRelatedVO(TopicEntity topic) {
        TopicPostRelatedVO vo = new TopicPostRelatedVO();
        vo.setImage(topic.getImage() != null ? topic.getImage() : "");
        vo.setTopicName(topic.getTopicName() != null ? topic.getTopicName() : "");
        vo.setPostCount(topic.getPostCount() != null ? topic.getPostCount() : 0);
        return vo;
    }

    private MentionedBookVO convertToMentionedBookVO(BookEntity book) {
        MentionedBookVO vo = new MentionedBookVO();
        vo.setCover(ImagePathUtils.processCoverPath(book.getCover()));
        vo.setBookTitle(book.getTitle() != null ? book.getTitle() : "");
        vo.setAuthorName(book.getAuthorName() != null ? book.getAuthorName() : "");
        vo.setAuthorId(book.getAuthorId() != null ? book.getAuthorId() : 0);
        vo.setRating(book.getRating() != null ? book.getRating() : 0);
        vo.setReadCount(book.getReadCount() != null ? book.getReadCount() : 0);
        vo.setDescription(book.getDescription() != null ? book.getDescription() : "");
        return vo;
    }

    @Override
    @Transactional
    public PostVO createPost(PostCreationDTO dto, Integer authorId) {
        // 验证用户存在
        UserEntity author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

        // 创建帖子
        PostEntity post = new PostEntity();
        post.setAuthorId(authorId);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        // 处理关联书籍
        if (dto.getBookIds() != null && !dto.getBookIds().isEmpty()) {
            List<BookEntity> books = bookRepository.findAllById(dto.getBookIds());
            post.setRelatedBooks(books);
        }

        // 处理话题 - 需要根据话题名称创建或获取TopicEntity
        if (dto.getTopics() != null && !dto.getTopics().isEmpty()) {
            // 这里需要处理话题关联
            // 暂时保存为字符串列表，后续需要改为关联TopicEntity
            // post.setRelatedTopics(dto.getTopics());
        }

        // 保存帖子
        PostEntity savedPost = postRepository.save(post);

        // 转换为VO返回
        return convertToPostVO(savedPost, authorId);
    }

    @Override
    @Transactional(readOnly = true)
    public PostVO getPostById(Integer postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "帖子不存在"));

        // 增加查看次数
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);

        return convertToPostVO(post, null);
    }

    @Override
    @Transactional(readOnly = true)
    public PostListVO getPostList(int page, int limit, String type, List<String> topics, Integer currentUserId) {
        // 参数验证
        if (page < 1)
            page = 1;
        if (limit < 1)
            limit = 20;
        if (limit > 100)
            limit = 100;

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<PostEntity> postPage;

        if ("following".equals(type) && currentUserId != null) {
            // 获取关注页帖子
            // 先获取关注列表
            List<FollowEntity> follows = followRepository.findByFollowerId(currentUserId, Pageable.unpaged())
                    .getContent();
            List<Integer> followingIds = follows.stream()
                    .map(FollowEntity::getFollowingId)
                    .collect(Collectors.toList());

            if (followingIds.isEmpty()) {
                postPage = Page.empty(pageable);
            } else {
                postPage = postRepository.findByAuthorIdInAndStatus(
                        followingIds, 1, pageable); // 状态1为正常
            }
        } else {
            // 获取广场帖子（全部帖子）
            postPage = postRepository.findByStatusOrderByCreatedAtDesc(1, pageable);
        }

        // 转换为VO
        List<PostVO> postVOs = postPage.getContent().stream()
                .map(post -> convertToPostVO(post, currentUserId))
                .collect(Collectors.toList());

        return new PostListVO(postVOs, postPage.getTotalElements(), page, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostSquareDTO> getSquarePosts(int page, int limit, String type, Integer currentUserId) {
        // 参数验证
        if (page < 1)
            page = 1;
        if (limit < 1)
            limit = 20;
        if (limit > 100)
            limit = 100;

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<PostEntity> postPage;

        if ("following".equals(type) && currentUserId != null) {
            // 获取关注页帖子
            List<FollowEntity> follows = followRepository.findByFollowerId(currentUserId, Pageable.unpaged())
                    .getContent();
            List<Integer> followingIds = follows.stream()
                    .map(FollowEntity::getFollowingId)
                    .collect(Collectors.toList());

            if (followingIds.isEmpty()) {
                postPage = Page.empty(pageable);
            } else {
                // 这里需要确保PostRepository有这个方法
                postPage = postRepository.findByAuthorIdInAndStatusOrderByCreatedAtDesc(
                        followingIds, 1, pageable);
            }
        } else {
            // 获取广场帖子（全部帖子）
            postPage = postRepository.findByStatusOrderByCreatedAtDesc(1, pageable);
        }

        // 转换为 PostSquareDTO
        return postPage.getContent().stream()
                .map(post -> convertToPostSquareDTO(post, currentUserId))
                .collect(Collectors.toList());
    }

    private PostSquareDTO convertToPostSquareDTO(PostEntity post, Integer currentUserId) {
        PostSquareDTO dto = new PostSquareDTO();
        dto.setPostId(post.getPostId());
        dto.setPostTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setPublishTime(post.getCreatedAt());
        dto.setPublishLocation(post.getPublishLocation());
        dto.setCommentCount(post.getCommentsCount());
        dto.setLikeCount(post.getLikesCount());

        // 获取作者信息
        UserEntity author = userRepository.findById(post.getAuthorId()).orElse(null);
        if (author != null) {
            AuthorInfoDTO authorInfo = new AuthorInfoDTO();
            authorInfo.setAuthorAvatar(author.getAvatar());
            authorInfo.setAuthorName(author.getUsername());
            dto.setAuthor(authorInfo);

            // 检查当前用户是否关注作者
            if (currentUserId != null) {
                boolean isFollowing = followRepository.findByFollowerIdAndFollowingId(currentUserId, author.getUserId())
                        .isPresent();
                dto.setIsFollowingAuthor(isFollowing);
            } else {
                dto.setIsFollowingAuthor(false);
            }
        }

        // 检查是否点赞
        if (currentUserId != null) {
            boolean isLiked = likeRepository.findByPostIdAndUserId(post.getPostId(), currentUserId)
                    .isPresent();
            dto.setIsLiked(isLiked);
        } else {
            dto.setIsLiked(false);
        }

        // 获取话题列表
        dto.setTopics(post.getTopics());

        // 获取提到的第一本书
        if (post.getRelatedBooks() != null && !post.getRelatedBooks().isEmpty()) {
            BookEntity firstBook = post.getRelatedBooks().get(0);
            BookSimpleDTO bookSimple = new BookSimpleDTO();
            bookSimple.setBookId(firstBook.getBookId());
            bookSimple.setBookTitle(firstBook.getTitle());
            bookSimple.setCover(ImagePathUtils.processCoverPath(firstBook.getCover()));
            bookSimple.setAuthorName(firstBook.getAuthor() != null ? firstBook.getAuthor().getAuthorName() : "未知作者");
            dto.setMentionedFirstBook(bookSimple);
        }

        return dto;
    }

    @Override
    @Transactional
    public boolean deletePost(Integer postId, Integer userId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "帖子不存在"));

        // 验证权限：只能删除自己的帖子
        if (!post.getAuthorId().equals(userId)) {
            return false;
        }

        // 软删除：设置删除时间
        post.setDeletedAt(LocalDateTime.now());
        post.setStatus(3); // 状态3为删除
        postRepository.save(post);

        return true;
    }

    @Override
    public Integer getUserPostCount(Integer userId) {
        return postRepository.countByAuthorId(userId);
    }

    // 辅助方法：转换为PostVO
    private PostVO convertToPostVO(PostEntity post, Integer currentUserId) {
        PostVO vo = new PostVO();
        vo.setPostId(post.getPostId());
        vo.setAuthorId(post.getAuthorId());
        vo.setTitle(post.getTitle());
        vo.setContentSummary(getContentSummary(post.getContent()));
        vo.setCreatedAt(post.getCreatedAt());
        vo.setLikesCount(post.getLikesCount());
        vo.setCommentsCount(post.getCommentsCount());

        // 获取作者信息
        UserEntity author = userRepository.findById(post.getAuthorId()).orElse(null);
        if (author != null) {
            // 设置作者相关信息
        }

        // 检查是否点赞
        if (currentUserId != null) {
            // 使用likeService检查
        }

        return vo;
    }

    private String getContentSummary(String content) {
        if (content == null || content.length() <= 100) {
            return content;
        }
        return content.substring(0, 100) + "...";
    }

    @Override
    @Transactional(readOnly = true)
    public BookSearchResponseDTO searchBooks(String keyword, Integer cursor, int limit) {
        // 参数验证
        if (keyword == null || keyword.trim().isEmpty()) {
            return new BookSearchResponseDTO(new ArrayList<>(), 0, false); // 改为整数0
        }

        if (limit <= 0)
            limit = 20;
        if (limit > 100)
            limit = 100;

        // 创建分页
        Pageable pageable = PageRequest.of(0, limit);

        // 搜索书籍
        Page<BookEntity> bookPage = bookRepository.searchBooks("%" + keyword + "%", pageable);

        // 转换为 DTO
        List<BookSearchResultDTO> bookDTOs = bookPage.getContent().stream()
                .map(this::convertToBookSearchResultDTO)
                .collect(Collectors.toList());

        // 生成游标（如果有下一页）
        Integer nextCursor = 0; // 默认值设为0
        boolean hasMore = bookPage.hasNext();

        if (hasMore) {
            // 使用最后一本书的ID作为游标
            BookEntity lastBook = bookPage.getContent().get(bookPage.getContent().size() - 1);
            nextCursor = lastBook.getBookId(); // 直接返回书的ID
        }

        return new BookSearchResponseDTO(bookDTOs, nextCursor, hasMore);
    }

    @Override
    @Transactional(readOnly = true)
    public TopicSearchResponseDTO searchTopics(String keyword, Integer cursor, int limit) {
        // 参数验证
        if (keyword == null || keyword.trim().isEmpty()) {
            return new TopicSearchResponseDTO(new ArrayList<>(), 0, false);
        }

        if (limit <= 0)
            limit = 20;
        if (limit > 100)
            limit = 100;

        // 创建分页
        Pageable pageable = PageRequest.of(0, limit);

        // 搜索话题
        Page<TopicEntity> topicPage = topicRepository.searchTopics("%" + keyword + "%", pageable);

        // 转换为 DTO
        List<TopicSearchResultDTO> topicDTOs = topicPage.getContent().stream()
                .map(this::convertToTopicSearchResultDTO)
                .collect(Collectors.toList());

        // 生成游标（如果有下一页）
        Integer nextCursor = 0; // 默认返回0
        boolean hasMore = topicPage.hasNext();

        if (hasMore) {
            // 使用最后一个话题的ID作为游标
            TopicEntity lastTopic = topicPage.getContent().get(topicPage.getContent().size() - 1);
            nextCursor = lastTopic.getTopicId(); // 直接返回话题ID
        }

        return new TopicSearchResponseDTO(topicDTOs, nextCursor, hasMore);
    }

    private BookSearchResultDTO convertToBookSearchResultDTO(BookEntity book) {
        BookSearchResultDTO dto = new BookSearchResultDTO();
        dto.setBookId(book.getBookId());
        dto.setBookTitle(book.getTitle());
        dto.setAuthorName(book.getAuthor() != null ? book.getAuthor().getAuthorName() : "未知作者");
        return dto;
    }

    private TopicSearchResultDTO convertToTopicSearchResultDTO(TopicEntity topic) {
        TopicSearchResultDTO dto = new TopicSearchResultDTO();
        dto.setTopicId(topic.getTopicId());
        dto.setTopicName(topic.getTopicName());
        dto.setViewCount(0); // 如果需要查看次数，需要在TopicEntity中添加字段
        dto.setDiscussionCount(topic.getPostCount() != null ? topic.getPostCount() : 0);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelatedBookDTO> getPostRelatedBooks(Integer postId) {
        // 验证帖子存在
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "帖子不存在"));

        // 获取相关书籍
        if (post.getRelatedBooks() == null || post.getRelatedBooks().isEmpty()) {
            return Collections.emptyList();
        }

        // 转换为DTO
        return post.getRelatedBooks().stream()
                .map(this::convertToRelatedBookDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserPostsResponseDTO getUserPosts(Integer userId, String cursor, int limit) {
        // 验证用户存在
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
        }

        // 解析游标
        Integer cursorId = null;
        if (cursor != null && !cursor.isEmpty()) {
            try {
                // 游标格式：post_123_时间戳
                String[] parts = cursor.split("_");
                if (parts.length >= 2) {
                    cursorId = Integer.parseInt(parts[1]);
                }
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "无效的游标格式");
            }
        }

        // 构建查询条件
        Pageable pageable = PageRequest.of(0, limit);
        List<PostEntity> posts;

        if (cursorId != null) {
            // 使用游标查询：postId < cursorId（按时间倒序，所以用小于）
            posts = postRepository.findByAuthorIdAndPostIdLessThanOrderByCreatedAtDesc(
                    userId, cursorId, pageable);
        } else {
            // 第一页查询
            posts = postRepository.findByAuthorIdOrderByCreatedAtDesc(userId, pageable);
        }

        // 转换为DTO
        List<PostSquareDTO> postDTOs = posts.stream()
                .map(post -> convertToPostSquareDTO(post, userId)) // 使用之前的方法
                .collect(Collectors.toList());

        // 计算是否还有更多数据
        boolean hasMore = false;
        String nextCursor = null;

        if (!posts.isEmpty()) {
            // 检查是否还有更多帖子
            PostEntity lastPost = posts.get(posts.size() - 1);
            long count = postRepository.countByAuthorIdAndPostIdLessThan(
                    userId, lastPost.getPostId());
            hasMore = count > 0;

            if (hasMore) {
                // 生成下一页的游标
                long timestamp = System.currentTimeMillis() / 1000;
                nextCursor = String.format("post_%d_%d", lastPost.getPostId(), timestamp);
            }
        }

        // 统计用户帖子相关数据
        Integer postCount = postRepository.countByUserId(userId);
        Integer commentCount = postRepository.sumCommentsByUserId(userId);
        Integer likeCount = postRepository.sumLikesByUserId(userId);

        return new UserPostsResponseDTO(
                postDTOs,
                hasMore,
                nextCursor,
                postCount != null ? postCount : 0,
                commentCount != null ? commentCount : 0,
                likeCount != null ? likeCount : 0);
    }

    private RelatedBookDTO convertToRelatedBookDTO(BookEntity book) {
        RelatedBookDTO dto = new RelatedBookDTO();
        dto.setBookId(book.getBookId());
        dto.setCover(ImagePathUtils.processCoverPath(book.getCover()));
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor() != null ? book.getAuthor().getAuthorName() : "未知作者");

        // 截取描述，避免过长
        if (book.getDescription() != null && book.getDescription().length() > 100) {
            dto.setDescription(book.getDescription().substring(0, 100) + "...");
        } else {
            dto.setDescription(book.getDescription());
        }

        return dto;
    }

    // 检查是否关注用户
    public boolean isFollowingUser(Integer followerId, Integer followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    // 检查是否关注话题
    public boolean isFollowingTopic(Integer userId, Integer topicId) {
        return topicFollowRepository.existsByUserIdAndTopicId(userId, topicId);
    }
}