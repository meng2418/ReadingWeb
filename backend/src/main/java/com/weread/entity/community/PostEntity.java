package com.weread.entity.community;

import com.weread.entity.base.BaseEntity;
import com.weread.entity.user.UserEntity;
import com.weread.entity.BookEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "post_info")
@Data
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
public class PostEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    // 关联用户
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    private String title;

    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    // 关联书本
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_book_mapping",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<BookEntity> relatedBooks;

    // 标签
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_topic_info", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "topic_name")
    private List<String> relatedTopics;

    private int likesCount = 0;
    private int commentsCount = 0;
    private int status = 0;
}
