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
    private Integer accessId;

    @Column(name = "chapter_id", nullable = false, unique = true)
    private Integer chapterId;

    @Column(name = "access_level", nullable = false)
    private String accessLevel = "free";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", insertable = false, updatable = false)
    private ChapterEntity chapter;
}
