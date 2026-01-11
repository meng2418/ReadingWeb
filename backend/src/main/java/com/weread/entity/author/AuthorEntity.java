package com.weread.entity.author;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Author Entity (corresponds to the 'author_info' table in the database).
 */
@Data
@Entity
@Table(name = "author_info") // Maps to the database table name
public class AuthorEntity {

    /**
     * Author ID (Primary Key, Auto Increment).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment strategy
    private Long authorId;

    /**
     * Author's name (Unique, Not Null).
     */
    @Column(unique = true, nullable = false) // Unique and not null constraint
    private String name;

    /**
     * Author's biography (Optional, supports long text).
     */
    @Column(columnDefinition = "TEXT") // Supports long text content
    private String bio;

    /**
     * Author's avatar (Optional, stores the image URL).
     */
    private String avatar;

}