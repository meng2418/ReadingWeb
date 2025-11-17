package com.weread.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import com.weread.entity.user.UserEntity;

/**
 * ���ʵ���ࣨ��Ӧ���ݿ�� bookshelf_info��
 * ���ڼ�¼�û����鼮�Ĺ�����ϵ�������Ķ�״̬��ʱ�����Ϣ
 */
@Data
@Entity
@Table(name = "bookshelf_info",
        // ΨһԼ����ͬһ�û������ظ�����ͬһ���飨ƥ�����ݿ�� @@unique([userId, bookId])��
        uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "book_id" }))
public class BookshelfEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookshelfid")
    private Integer bookshelfId; // ��ܼ�¼ID��������������

    @Column(name = "user_id", nullable = false)
    private Integer userId; // �����û�ID�������

    @Column(name = "book_id", nullable = false)
    private Integer bookId; // �����鼮ID�������

    @Column(nullable = false)
    private String status; // �Ķ�״̬��reading���Ķ��У���unread��δ������finished������ɣ���Ĭ��reading

    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt; // ���ӵ���ܵ�ʱ�䣬Ĭ�ϵ�ǰʱ��

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt; // ����Ķ�ʱ�䣬��Ϊnull

    // ������ϵ���������ӣ����������أ�
    /**
     * ���û����Ĺ��������һ�������ܼ�¼����һ���û���
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user; // �������û�ʵ��

    /**
     * ���鼮���Ĺ��������һ�������ܼ�¼����ͬһ���飩
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", insertable = false, updatable = false)
    private BookEntity book; // �������鼮ʵ��

    // �Զ����Ĭ��ֵ
    @PrePersist
    protected void onCreate() {
        // ��������ʱ��Ϊ��ǰʱ�䣨��δ�ֶ����ã�
        if (addedAt == null) {
            addedAt = LocalDateTime.now();
        }
        // ����Ĭ���Ķ�״̬Ϊreading
        if (status == null || status.trim().isEmpty()) {
            status = "reading";
        }
    }
}