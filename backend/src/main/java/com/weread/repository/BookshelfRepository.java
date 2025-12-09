package com.weread.repository;

import com.weread.entity.BookshelfEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookshelfRepository extends JpaRepository<BookshelfEntity, Integer> {

    // 1. ����鼮�Ƿ����û������
    Optional<BookshelfEntity> findByUserIdAndBookId(Long userId, Integer bookId);

    // 2. ��ѯ�û�����е������鼮��ȫ����ܣ�
    List<BookshelfEntity> findByUserId(Long userId);

    // 3. ��״̬ɸѡ�û�����е��鼮��δ��/�Ķ���/����ɣ�
    List<BookshelfEntity> findByUserIdAndStatus(Long userId, String status);

    // 4. �����鼮������е�״̬�����reading��Ϊfinished��
    @Modifying
    @Query("UPDATE BookshelfEntity b SET b.status = :status, b.lastReadAt = :lastReadAt " +
            "WHERE b.userId = :userId AND b.bookId = :bookId")
    void updateBookStatus(
            @Param("userId") Long userId,
            @Param("bookId") Integer bookId,
            @Param("status") String status,
            @Param("lastReadAt") LocalDateTime lastReadAt);
}