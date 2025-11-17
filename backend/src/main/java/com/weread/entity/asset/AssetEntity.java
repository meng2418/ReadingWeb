package com.weread.entity.asset;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Data;
@Data
@Entity
public class AssetEntity {
    @Id
    private Long userId; // 用户ID作为主键，与UserEntity一对一
    private Integer coins; // 书币余额
    private LocalDateTime updatedAt;
}
