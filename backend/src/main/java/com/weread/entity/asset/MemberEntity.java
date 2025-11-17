package com.weread.entity.asset;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String level; // 会员等级 (e.g., "VIP")
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
