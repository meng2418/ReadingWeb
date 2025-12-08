package com.weread.service.community;

import com.weread.dto.community.PostCreationDTO;
import com.weread.vo.community.PostListVO;
import com.weread.vo.community.PostVO;
import java.util.List;

public interface PostService {
    
    /**
     * 发帖
     */
    PostVO createPost(PostCreationDTO dto, Long authorId);

    /**
     * 获取单个帖子详情
     */
    PostVO getPostById(Long postId);

    /**
     * 获取帖子列表并根据类型筛选
     * @param currentUserId 当前登录用户的ID (如果未登录，则为 null)
     */
    PostListVO getPostList(int page, int limit, String type, 
                                 List<String> hashtags, Long currentUserId);
}