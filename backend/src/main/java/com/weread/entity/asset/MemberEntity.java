package com.weread.entity.asset;

import com.weread.entity.base.BaseEntity;
import com.weread.entity.user.UserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "member_info")
public class MemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer memberId;

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "expire_date")
    private LocalDateTime expireDate;

    @Column(nullable = false)
    private Integer level = 1;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;
}
