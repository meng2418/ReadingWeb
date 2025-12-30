package com.weread.repository.community;

import com.weread.entity.community.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {

    /**
     * 查找一个帖子下所有一级评论 (按时间倒序)
     */
    Page<CommentEntity> findByPostIdAndParentCommentIdIsNullOrderByCreatedAtDesc(Integer postId, Pageable pageable);

    /**
     * 查找某条一级评论下的所有回复 (二级评论) (按时间升序)
     */
    List<CommentEntity> findByParentCommentIdOrderByCreatedAtAsc(Integer parentCommentId, Pageable pageable);

    /**
     * 统计某个父评论下的回复总数
     */
    long countByParentCommentId(Integer parentCommentId);
    
    /**
     * 统计一个帖子的评论总数
     */
    long countByPostId(Integer postId);
}