package com.weread.entity.note;

import com.weread.entity.user.UserEntity;
import com.weread.entity.BookEntity;
import com.weread.entity.ChapterEntity;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "note_info") // 对应 Prisma 的 Note_info
public class NoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noteId;

    @Column(nullable = false)
    private Long userId; // 笔记创建者 ID

    @Column(nullable = false)
    private Integer bookId; // 笔记所属书籍 ID (使用 Integer 匹配 BookEntity ID)

    private Integer chapterId; // 笔记所属章节 ID (使用 Integer 匹配 ChapterEntity ID)

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 笔记内容

    @Column(nullable = false)
    private String type = "highlight"; // highlight, comment

    private String color; // 标注颜色 (可选)

    private Integer page; // 页码 (可选)

    @Column(nullable = false)
    private Boolean isPublic = false; // 是否公开

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // --- 关联关系 ---
    
    // 用户
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private UserEntity user;

    // 书籍
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookId", insertable = false, updatable = false)
    private BookEntity book;

    // 章节
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapterId", insertable = false, updatable = false)
    private ChapterEntity chapter;
    
    // Likes 关联（省略 OneToMany 列表，因为它不是笔记的核心属性）
}