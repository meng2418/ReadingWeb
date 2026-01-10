package com.weread.service.author;

import com.weread.vo.author.AuthorDetailVO;

/**
 * 作者服务接口
 */
public interface AuthorService {
    
    /**
     * 获取作者详情
     * @param authorId 作者ID
     * @param currentUserId 当前登录用户ID（可为null）
     * @return 作者详情
     */
    AuthorDetailVO getAuthorDetail(Integer authorId, Integer currentUserId);
}

