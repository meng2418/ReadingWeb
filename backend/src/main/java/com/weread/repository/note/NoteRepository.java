package com.weread.repository.note;

import com.weread.entity.note.NoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, Long> {

    /**
     * 查询某个用户的所有笔记，按创建时间降序排列
     */
    Page<NoteEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 查询某本书籍的所有公开笔记
     */
    Page<NoteEntity> findByBookIdAndIsPublicTrueOrderByCreatedAtDesc(Integer bookId, Pageable pageable);

    /**
     * 查询某一章节的笔记（包括私有和公开的）
     */
    List<NoteEntity> findByChapterIdOrderByCreatedAtDesc(Integer chapterId);

    Integer countByUserId(Integer userId);
}