package com.weread.entity.community;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.weread.entity.BookEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Post_info")
@Data
@EntityListeners(AuditingEntityListener.class)
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    // 谁发的帖子
    private Long authorId;

    // 帖子标题
    private String title;

    // 帖子内容
    @Lob // 大文本字段
    private String content;

    // --- 【修改点 1: 关联书本ID列表 -> @ManyToMany 关系】 ---
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "post_book_mapping", // 中间表名
        joinColumns = @JoinColumn(name = "postId"),
        inverseJoinColumns = @JoinColumn(name = "bookId")
    )
    private List<BookEntity> relatedBooks; 
    
    // --- 【修改点 2: 标签列表 -> @ElementCollection】 ---
    @ElementCollection(fetch = FetchType.EAGER) // 简单字段可以 EAGER 加载
    @CollectionTable(name = "post_hashtag", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag_name")
    private List<String> hashtags;
    // 统计数据
    private long likesCount = 0;
    private long commentsCount = 0;
    
    // 帖子状态 (例如：0-正常, 1-审核中, 2-已删除)
    private int status = 0; 
    
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}