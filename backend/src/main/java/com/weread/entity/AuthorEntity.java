package com.weread.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * ����ʵ���ࣨ��Ӧ���ݿ�� author_info��
 */
@Data
@Entity
@Table(name = "author_info") // ӳ�����ݿ����
public class AuthorEntity {

    /**
     * ����ID��������������
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // �������ԣ���Ӧ @default(autoincrement())
    private Integer authorId;

    /**
     * ����������Ψһ��
     */
    @Column(unique = true, nullable = false) // ��Ӧ @unique���ǿ�
    private String name;

    /**
     * ���߼�飨���ı�����ѡ��
     */
    @Column(columnDefinition = "TEXT") // ��Ӧ @db.Text��֧�ֳ��ı�
    private String bio;

    /**
     * ����ͷ�񣨿�ѡ���洢ͼƬURL��
     */
    private String avatar;

}