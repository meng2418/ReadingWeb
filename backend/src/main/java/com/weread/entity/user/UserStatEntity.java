package com.weread.entity.user;

import com.weread.entity.base.BaseEntity; // 假设有一个包含 createdAt/updatedAt 的 BaseEntity
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "UserStat_info")
public class UserStatEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statId;

    @Column(nullable = false, unique = true)
    private Long userId; // 与用户ID一对一关联

    @Column(nullable = false)
    private Integer totalBooksCompleted = 0; // 累计阅读书籍数量

    @Column(nullable = false)
    private Integer totalNotesCount = 0; // 累计笔记数量

    // 单位：分钟
    @Column(nullable = false)
    private Integer totalReadingDurationMinutes = 0; // 累计阅读时长

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId", insertable = false, updatable = false)
    private UserEntity user;
}