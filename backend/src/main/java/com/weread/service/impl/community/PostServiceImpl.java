package com.weread.service.impl.community;

import com.weread.dto.community.PostCreationDTO;
import com.weread.vo.book.AuthorVO;
import com.weread.vo.book.MentionedBookVO;
import com.weread.vo.community.PostListVO;
import com.weread.entity.BookEntity;
import com.weread.entity.community.PostEntity;
import com.weread.entity.community.TopicEntity;
import com.weread.entity.user.UserEntity;
import com.weread.repository.BookRepository;
import com.weread.repository.community.LikeRepository;
import com.weread.repository.user.UserRepository;
import com.weread.repository.community.PostRepository;
import com.weread.repository.user.FollowRepository;
import com.weread.repository.community.TopicRepository;
import com.weread.service.book.BookService;
import com.weread.service.community.PostService;
import com.weread.service.community.FollowService;
import com.weread.service.community.LikeService;
import com.weread.vo.community.PostVO;
import com.weread.vo.community.TopicPostVO;
import com.weread.vo.community.TopicPostRelatedVO;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final BookService bookService;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final BookRepository bookRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;  // 新增
    private final FollowService followService;      // 新增，需要创建
    private final LikeService likeService;          // 新增，需要创建

    public PostServiceImpl(BookService bookService, PostRepository postRepository,
            FollowRepository followRepository, BookRepository bookRepository,
            LikeRepository likeRepository, UserRepository userRepository,
            TopicRepository topicRepository, FollowService followService, LikeService likeService) {
        this.bookService = bookService;
        this.postRepository = postRepository;
        this.followRepository = followRepository;
        this.bookRepository = bookRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;      // 新增
        this.followService = followService;          // 新增
        this.likeService = likeService;              // 新增
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
    if (limit == null || limit <= 0) limit = 20;
    if (limit > 100) limit = 100;
    
    // 验证sort参数
    if (!List.of("latest", "hot", "quality").contains(sort)) {
        sort = "latest";
    }
    
    List<PostEntity> posts;
    
    if (cursor != null && cursor > 0) {
        // cursor-based 查询：postId > cursor
        Pageable pageable = PageRequest.of(0, limit);
        posts = postRepository.findPostsByTopicAndCursor(topicId, sort, cursor, limit, pageable);
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
    
    private TopicPostVO convertToTopicPostVO(PostEntity post, List<UserEntity> authors, Integer currentUserId, Integer currentTopicId) {
        TopicPostVO vo = new TopicPostVO();
        
        // 基本字段
        vo.setPostId(post.getPostId());
        vo.setPostTitle(post.getTitle());
        vo.setContent(post.getContent());
        vo.setPublishTime(post.getCreatedAt());
        vo.setPublishLocation("");  // 如果PostEntity没有location字段，先设为空
        
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
            
            // 是否已关注作者（需要FollowService的实现）
            if (currentUserId != null && followService != null) {
                try {
                    vo.setIsFollowingAuthor(followService.isFollowing(currentUserId, author.getUserId()));
                } catch (Exception e) {
                    vo.setIsFollowingAuthor(false);  // 出错时默认为false
                }
            } else {
                vo.setIsFollowingAuthor(false);
            }
        }
        
        // 是否已点赞（需要LikeService的实现）
        if (currentUserId != null && likeService != null) {
            try {
                vo.setIsLiked(likeService.isLiked(currentUserId, post.getPostId()));
            } catch (Exception e) {
                vo.setIsLiked(false);  // 出错时默认为false
            }
        } else {
            vo.setIsLiked(false);
        }
        
        // 帖子的话题列表（需要PostEntity有postTopics关联）
        try {
            // 假设PostEntity有getPostTopics方法
            vo.setTopics(Collections.emptyList());  // 先设为空列表
        } catch (Exception e) {
            vo.setTopics(Collections.emptyList());
        }
        
        // 提到的书籍（需要PostEntity有mentionedBooks关联）
        try {
            vo.setMentionedBooks(Collections.emptyList());  // 先设为空列表
        } catch (Exception e) {
            vo.setMentionedBooks(Collections.emptyList());
        }
        
        // 相关话题（从数据库中查询相关话题）
        try {
            List<TopicEntity> relatedTopics = topicRepository.findRelatedTopicsByPostId(post.getPostId(), currentTopicId, 5);
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
        vo.setCover(book.getCover() != null ? book.getCover() : "");
        vo.setBookTitle(book.getTitle() != null ? book.getTitle() : "");
        vo.setAuthorName(book.getAuthorName() != null ? book.getAuthorName() : "");
        vo.setAuthorId(book.getAuthorId() != null ? book.getAuthorId() : 0);
        vo.setRating(book.getRating() != null ? book.getRating() : 0);
        vo.setReadCount(book.getReadCount() != null ? book.getReadCount() : 0);
        vo.setDescription(book.getDescription() != null ? book.getDescription() : "");
        return vo;
    }

    @Override
    public PostVO createPost(PostCreationDTO dto, Integer authorId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createPost'");
    }

    @Override
    public PostVO getPostById(Integer postId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPostById'");
    }

    @Override
    public PostListVO getPostList(int page, int limit, String type, List<String> topics, Integer currentUserId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPostList'");
    }
    
    // ========================================================
    // 其他原有方法保持不变...
    // ========================================================
    
    // 原有的createPost、getPostById、getPostList等方法保持不变
    // 原有的辅助方法也保持不变
}