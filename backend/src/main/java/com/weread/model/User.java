package com.weread.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    private String nickname;
    private String avatar;
    private String bio;
    
    @Column(name = "is_member")
    private Boolean isMember = false;
    
    private Integer coins = 0;
    
    @Column(name = "total_reading_time")
    private Integer totalReadingTime = 0;
}