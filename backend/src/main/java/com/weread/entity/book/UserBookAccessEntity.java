package com.weread.entity.book;

import com.weread.entity.base.BaseEntity;
import com.weread.entity.user.UserEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_book_access_info", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "book_id" })
})
public class UserBookAccessEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accessId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "book_id", nullable = false)
    private Integer bookId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", insertable = false, updatable = false)
    private BookEntity book;
}
