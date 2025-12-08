package com.weread.entity.book;

import com.weread.entity.base.BaseEntity;
import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "user_book_access_info", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userId", "bookId"})
})
public class UserBookAccessEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accessId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long bookId;

    // BaseEntity 会提供 createdAt (purchaseTime)
}