package com.weread.entity.book;

import com.weread.entity.base.BaseEntity;
import com.weread.entity.user.UserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

/**
 * 书评实体
 */
@Entity
@Table(name = "book_review_info")
@Data
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
public class BookReviewEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    @Column(name = "book_id", nullable = false)
    private Integer bookId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /**
     * 评分：recommend(推荐), average(一般), not_recommend(不推荐)
     */
    @Column(name = "rating", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Rating rating;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 是否公开：true-公开，false-仅自己可见
     */
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    /**
     * 状态：1-正常，2-隐藏，3-删除
     */
    @Column(name = "status", nullable = false)
    private Integer status = 1;

    // 关联实体
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    /**
     * 评分枚举
     */
    public enum Rating {
        recommend,      // 推荐
        average,        // 一般
        not_recommend   // 不推荐
    }
}

