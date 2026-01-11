package com.weread.entity.book;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "readingstatus_info")
public class ReadingStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "readingstatus_id")
    private Integer readingStatusId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime date = LocalDateTime.now();

    @Column(name = "readingtime")
    private Integer readingTime = 0; //

}