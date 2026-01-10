package com.weread.service.community;

import com.weread.dto.community.BookSearchResponseDTO;
import com.weread.dto.community.PostCreationDTO;
import com.weread.dto.community.PostSquareDTO;
import com.weread.dto.community.RelatedBookDTO;
import com.weread.dto.community.TopicSearchResponseDTO;
import com.weread.dto.community.UserPostsResponseDTO;
import com.weread.vo.community.PostListVO;
import com.weread.vo.community.PostVO;
import com.weread.vo.community.TopicPostVO;

import java.util.List;

public interface PostService {
    
    /**
     * 发帖
     * @param dto 帖子创建 DTO
     * @param authorId 作者用户 ID
     * @return 创建后的帖子 VO
     */
    PostVO createPost(PostCreationDTO dto, Integer authorId);

    /**
     * 获取单个帖子详情
     * @param postId 帖子 ID
     * @return 帖子 VO
     */
    PostVO getPostById(Integer postId);

    /**
     * 获取帖子列表并根据类型和标签筛选
     * @param page 页码，从 1 开始
     * @param limit 每页条数
     * @param type 筛选类型: "all" / "mine" / "following"
     * @param topics 标签列表 (帖子必须包含所有指定标签)
     * @param currentUserId 当前登录用户的 ID (未登录可为 null)
     * @return 帖子列表 VO
     */
    PostListVO getPostList(int page, int limit, String type, 
                           List<String> topics, Integer currentUserId);

    List<TopicPostVO> getTopicPosts(Integer topicId, String sort, Integer cursor, Integer limit, Integer currentUserId);

    /**
     * 删除帖子
     * @param postId 帖子ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否删除成功
     */
    boolean deletePost(Integer postId, Integer userId);
    
    /**
     * 获取用户帖子数量
     * @param userId 用户ID
     * @return 帖子数量
     */
    Integer getUserPostCount(Integer userId);

    /**
     * 搜索书籍
     */
    BookSearchResponseDTO searchBooks(String keyword, Integer cursor, int limit);
    
    /**
     * 搜索话题
     */
    TopicSearchResponseDTO searchTopics(String keyword, Integer cursor, int limit);

    List<PostSquareDTO> getSquarePosts(int page, int limit, String mappedType, Integer currentUserId);

    /**
     * 获取帖子相关书籍列表
     */
    List<RelatedBookDTO> getPostRelatedBooks(Integer postId);
    
    /**
     * 获取用户帖子瀑布流
     */
    UserPostsResponseDTO getUserPosts(Integer userId, String cursor, int limit);
}
