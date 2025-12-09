package com.weread.repository.community;

import com.weread.entity.community.LikeEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {

    // ----------------------
    // 帖子点赞
    // ----------------------
    Optional<LikeEntity> findByPostIdAndUserId(Long postId, Long userId);

    long countByPostId(Long postId);

    @Query("SELECT l.userId FROM LikeEntity l WHERE l.postId = :postId ORDER BY l.createdAt DESC")
    List<Long> findTopNUserIdsByPostId(Long postId, Pageable pageable);

    // ----------------------
    // 评论点赞
    // ----------------------
    Optional<LikeEntity> findByCommentIdAndUserId(Long commentId, Long userId);

    long countByCommentId(Long commentId);

    @Query("SELECT l.userId FROM LikeEntity l WHERE l.commentId = :commentId ORDER BY l.createdAt DESC")
    List<Long> findTopNUserIdsByCommentId(Long commentId, Pageable pageable);

    // ----------------------
    // 笔记点赞 (noteId 是 Integer)
    // ----------------------
    Optional<LikeEntity> findByNoteIdAndUserId(Integer noteId, Long userId);

    long countByNoteId(Integer noteId);

    @Query("SELECT l.userId FROM LikeEntity l WHERE l.noteId = :noteId ORDER BY l.createdAt DESC")
    List<Long> findTopNUserIdsByNoteId(Integer noteId, Pageable pageable);
}
