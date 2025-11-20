package com.weread.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "readingstatus_info")
public class ReadingStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "readingstatusid")
    private Integer readingStatusId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private LocalDateTime date = LocalDateTime.now();

    @Column(name = "readingtime")
    private Integer readingTime = 0; // ∑÷÷”

}