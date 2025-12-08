package com.weread.entity.book;

import com.weread.entity.ChapterEntity;
import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "chapter_access_info")
public class ChapterAccessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accessId;

    @Column(nullable = false, unique = true)
    private Long chapterId; // 章节ID

    /**
     * 访问级别：free, trial, member
     */
    @Column(nullable = false)
    private String accessLevel = "free";

    // 关联关系
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapterId", insertable = false, updatable = false)
    private ChapterEntity chapter; 
}