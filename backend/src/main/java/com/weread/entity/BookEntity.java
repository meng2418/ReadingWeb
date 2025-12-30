package com.weread.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Book Entity (corresponds to the 'book_info' table in the database).
 */
@Data
@Entity
@Table(name = "book_info") 
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "book_id") 
    private Integer bookId;

    @Column(nullable = false) 
    private String title; 

    // 外键字段，用于数据插入和更新
    @Column(name = "author_id", nullable = false) 
    private Long authorId;
    
    // JPA 关联：只用于查询，不参与插入/更新
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", insertable = false, updatable = false) 
    private AuthorEntity author; // 关联作者实体

    private String cover; 

    @Column(columnDefinition = "TEXT") 
    private String description; 

    @Column(name = "category_id", nullable = false) 
    private Integer categoryId;

    private String publisher; 

    @Column(name = "publish_date")
    private LocalDateTime publishDate; 

    private String isbn; 

    @Column(name = "word_count")
    private Integer wordCount; 

    private Integer price; 

    @Column(name = "is_free")
    private Boolean isFree; 

    private Float rating; 

    @Column(name = "read_count")
    private Integer readCount; 

    @Column(name = "created_at", updatable = false) 
    private LocalDateTime createdAt;   //需要一个最近打开时间

    // Custom logic to set creation time and default values if not set by the database
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (price == null) {
            price = 0;
        }
        if (isFree == null) {
            isFree = false;
        }
        if (rating == null) {
            rating = 0f;
        }
        if (readCount == null) {
            readCount = 0;
        }
    }
}