package com.weread.repository.community;

import com.weread.entity.community.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    /**
     * 查询某个用户的所有消息，按时间降序排列
     */
    Page<MessageEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 查询某个用户未读消息的总数
     */
    long countByUserIdAndIsReadFalse(Long userId);

    /**
     * 【@Modifying】将指定消息标记为已读
     */
    @Modifying
    @Query("UPDATE MessageEntity m SET m.isRead = TRUE WHERE m.messageId = :messageId AND m.userId = :userId")
    int markAsRead(@Param("messageId") Integer messageId, @Param("userId") Long userId);

    /**
     * 【@Modifying】将用户的所有未读消息标记为已读（一键已读）
     */
    @Modifying
    @Query("UPDATE MessageEntity m SET m.isRead = TRUE WHERE m.userId = :userId AND m.isRead = FALSE")
    int markAllAsRead(@Param("userId") Long userId);
}