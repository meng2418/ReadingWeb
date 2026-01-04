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
    Optional<LikeEntity> findByPostIdAndUserId(Integer postId, Integer userId);

    @Query("SELECT l.userId FROM LikeEntity l WHERE l.postId = :postId ORDER BY l.createdAt DESC")
    List<Integer> findTopNUserIdsByPostId(Integer postId, Pageable pageable);

    // ----------------------
    // 评论点赞
    // ----------------------
    Optional<LikeEntity> findByCommentIdAndUserId(Integer commentId, Integer userId);

    @Query("SELECT l.userId FROM LikeEntity l WHERE l.commentId = :commentId ORDER BY l.createdAt DESC")
    List<Integer> findTopNUserIdsByCommentId(Integer commentId, Pageable pageable);

    // ----------------------
    // 笔记点赞 (noteId 是 Integer)
    // ----------------------
    Optional<LikeEntity> findByNoteIdAndUserId(Integer noteId, Integer userId);

    int countByNoteId(Integer noteId);

    @Query("SELECT l.userId FROM LikeEntity l WHERE l.noteId = :noteId ORDER BY l.createdAt DESC")
    List<Integer> findTopNUserIdsByNoteId(Integer noteId, Pageable pageable);

    // 添加按用户和帖子查询的方法
    boolean existsByUserIdAndPostId(Integer userId, Integer postId);

    // 按帖子ID查询点赞列表
    List<LikeEntity> findByPostId(Integer postId);
    
    // 按帖子ID查询点赞列表（按时间倒序）
    List<LikeEntity> findByPostIdOrderByCreatedAtDesc(Integer postId);
    
    // 统计帖子点赞数
    Integer countByPostId(Integer postId);
    
    // 按评论ID查询点赞列表
    List<LikeEntity> findByCommentId(Integer commentId);
    
    // 统计评论点赞数
    Integer countByCommentId(Integer commentId);
}
