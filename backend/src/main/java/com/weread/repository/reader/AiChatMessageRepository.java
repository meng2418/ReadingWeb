package com.weread.repository.reader;

import com.weread.entity.reader.AiChatMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiChatMessageRepository extends JpaRepository<AiChatMessageEntity, Long> {

    Page<AiChatMessageEntity> findByUserIdAndBookId(Integer userId, Integer bookId, Pageable pageable);

    Page<AiChatMessageEntity> findByUserIdAndBookIdAndIdLessThan(
            Integer userId,
            Integer bookId,
            Long id,
            Pageable pageable
    );
}
