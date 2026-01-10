package com.weread.repository.author;

import com.weread.entity.author.UserFollowAuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户关注作者 Repository
 */
@Repository
public interface UserFollowAuthorRepository extends JpaRepository<UserFollowAuthorEntity, Long> {

    /**
     * 检查用户是否关注了作者
     */
    Optional<UserFollowAuthorEntity> findByUserIdAndAuthorId(Integer userId, Integer authorId);

    /**
     * 统计作者的关注者数量
     */
    int countByAuthorId(Integer authorId);

    /**
     * 删除关注关系
     */
    void deleteByUserIdAndAuthorId(Integer userId, Integer authorId);
}

