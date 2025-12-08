package com.weread.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * �鼮ʵ���ࣨ��Ӧ���ݿ�� book_info��
 */
@Data
@Entity
@Table(name = "Book_info") // �����ݿ��������һ��
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ��������
    @Column(name = "book_id") // ӳ�����ݿ��ֶ� bookId
    private Integer bookId;

    @Column(nullable = false) // �ǿ��ֶ�
    private String title; // �鼮����

    @Column(name = "author_id", nullable = false) // ����ֶΣ��������߱�
    private Integer authorId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorId", insertable = false, updatable = false) // 使用 authorId 作为外键
    private AuthorEntity author;

    private String cover; // ����ͼƬ������Ϊnull��

    @Column(columnDefinition = "TEXT") // ���ı����ͣ���Ӧ���ݿ��@db.Text
    private String description; // �鼮��飨����Ϊnull��

    @Column(name = "category_id", nullable = false) // ����ֶΣ����������
    private Integer categoryId;

    private String publisher; // �����磨����Ϊnull��

    @Column(name = "publish_date")
    private LocalDateTime publishDate; // �������ڣ�����Ϊnull��

    private String isbn; // ISBN��ţ�����Ϊnull��

    @Column(name = "word_count")
    private Integer wordCount; // ����

    private Integer price; // ��Ҽ۸�Ĭ��0

    @Column(name = "is_free")
    private Boolean isFree; // �Ƿ���ѣ�Ĭ��false

    private Float rating; // �Ƽ�ֵ��Ĭ��0

    @Column(name = "read_count")
    private Integer readCount; // �Ķ�������Ĭ��0

    @Column(name = "created_at", updatable = false) // ����������
    private LocalDateTime createdAt; // ����ʱ�䣬Ĭ�ϵ�ǰʱ��

    // �Զ���䴴��ʱ�䣨������ݿ�δ����Ĭ��ֵ��
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        // ��ʼ��Ĭ��ֵ��������ݿ�δ����Ĭ��ֵ��
        if (price == null) {
            price = 0;
        }
        if (isFree == null) {
            isFree = false;
        }
        if (rating == null) {
            rating = 0f;
        }
        if (readCount == null) {
            readCount = 0;
        }
    }
}