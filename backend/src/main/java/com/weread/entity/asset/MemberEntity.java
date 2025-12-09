package com.weread.entity.asset;

import com.weread.entity.base.BaseEntity; 
import com.weread.entity.user.UserEntity; // 假设 UserEntity 存在
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户会员状态实体
 * 记录用户是否是付费会员及其到期时间。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "member_info")
// 假设继承了 BaseEntity (包含 createdAt, updatedAt)
public class MemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer memberId;

    /**
     * 用户ID，与 UserEntity 形成一对一关系，且必须唯一。
     */
    @Column(unique = true, nullable = false)
    private Long userId;

    /**
     * 会员过期时间。
     * 业务判断：如果 expireDate > 当前时间，则会员有效。
     */
    @Column(nullable = true) // 允许为空，表示用户从未开通过正式会员
    private LocalDateTime expireDate; 
    
    /**
     * 会员等级，默认为 1。
     */
    @Column(nullable = false)
    private Integer level = 1; 

    // --- JPA 关联（可选，用于方便地通过 MemberEntity 访问 UserEntity） ---
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId", insertable = false, updatable = false)
    private UserEntity user;
}