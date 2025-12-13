package com.weread.entity.user;

import com.weread.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_stat_info")
public class UserStatEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_stat_id") // 明确映射到数据库中的 user_stat_id 列
    private Integer userStatId;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private Integer totalBooksCompleted = 0;

    @Column(nullable = false)
    private Integer totalNotesCount = 0;

    @Column(nullable = false)
    private Integer totalReadingDurationMinutes = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;
}
