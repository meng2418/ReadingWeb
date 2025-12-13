CREATE DATABASE IF NOT EXISTS wechatreading_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE wechatreading_db;

CREATE TABLE user_info (
    user_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    
    username VARCHAR(50) NOT NULL UNIQUE,
    phone VARCHAR(20) UNIQUE,
    password VARCHAR(255) NOT NULL,
    
    avatar VARCHAR(255),
    bio VARCHAR(255),

    is_member BOOLEAN DEFAULT FALSE,
    member_end_date DATETIME NULL,
    
    total_reading_minutes INT DEFAULT 0,
    coins INT DEFAULT 0,

    -- Soft delete field
    is_deleted TINYINT(1) DEFAULT 0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE member_info (
    member_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    
    fk_user_id BIGINT UNSIGNED NOT NULL UNIQUE,
    level INT DEFAULT 1 COMMENT '会员等级，例如：1=普通会员, 2=高级会员，支持扩展',
    expire_at DATETIME NOT NULL COMMENT '会员到期时间',

    -- Soft Delete
    is_deleted TINYINT(1) DEFAULT 0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_member_user FOREIGN KEY (fk_user_id) REFERENCES user_info(user_id)
);

