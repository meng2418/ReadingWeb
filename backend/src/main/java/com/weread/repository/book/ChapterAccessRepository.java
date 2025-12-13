package com.weread.repository.book;

import com.weread.entity.book.ChapterAccessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChapterAccessRepository extends JpaRepository<ChapterAccessEntity, Long> {

    /**
     * 根据章节ID查找访问权限配置
     */
    Optional<ChapterAccessEntity> findByChapterId(Long chapterId);
}