package com.weread.entity.base;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@MappedSuperclass // 【核心】将属性映射到继承的子类表结构中
@EntityListeners(AuditingEntityListener.class) // 【核心】开启JPA审计，自动填充时间
public abstract class BaseEntity { // 使用抽象类，不对应任何数据库表

    @CreatedDate
    @Column(nullable = false, updatable = false) // 创建时间不可更新
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}