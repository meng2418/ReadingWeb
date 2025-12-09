package com.weread.entity.book;

import com.weread.entity.base.BaseEntity;
import com.weread.entity.user.UserEntity;
import com.weread.entity.BookEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_book_access_info", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userId", "bookId"})
})
public class UserBookAccessEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accessId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer bookId;

    // BaseEntity 会提供 createdAt (purchaseTime)

    // 💡 提示：如果需要 JPA 关联，您可以添加：
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookId", referencedColumnName = "bookId", insertable = false, updatable = false)
    private BookEntity book;
    
}