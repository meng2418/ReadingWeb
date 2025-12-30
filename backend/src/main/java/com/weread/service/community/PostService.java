package com.weread.service.community;

import com.weread.dto.community.PostCreationDTO;
import com.weread.vo.community.PostListVO;
import com.weread.vo.community.PostVO;
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
}
