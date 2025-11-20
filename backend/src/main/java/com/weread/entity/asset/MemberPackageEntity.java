package com.weread.entity.asset;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
public class MemberPackageEntity {
    @Id
    private Long id;
    private String name;
    private Integer durationDays;
    private Integer priceCents; // 价格（分）
    private Boolean isAvailable; // 是否在售
}
