package com.weread.entity.asset;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
public class RechargePackageEntity {
    @Id
    private Long id;
    private String name;
    private Integer priceCents;
    private Integer acquiredCoins; // 获得的书币数量
    private Boolean isAvailable;
}
