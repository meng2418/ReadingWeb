package com.weread.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

/**
 * 作者实体类（对应数据库表 author_info）
 */
@Data
@Entity
@Table(name = "author_info") // 映射数据库表名
public class AuthorEntity {

    /**
     * 作者ID（主键，自增）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增策略，对应 @default(autoincrement())
    private Integer authorId;

    /**
     * 作者姓名（唯一）
     */
    @Column(unique = true, nullable = false) // 对应 @unique，非空
    private String name;

    /**
     * 作者简介（长文本，可选）
     */
    @Column(columnDefinition = "TEXT") // 对应 @db.Text，支持长文本
    private String bio;

    /**
     * 作者头像（可选，存储图片URL）
     */
    private String avatar;

}