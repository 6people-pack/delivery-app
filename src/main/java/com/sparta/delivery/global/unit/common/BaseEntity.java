package com.sparta.delivery.global.unit.common;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false)
    protected Long createdBy;

    @LastModifiedBy
    protected Long updatedBy;

    protected Long deletedBy;

    protected LocalDateTime deletedAt;

    public void delete(Long userId) {
        this.deletedBy = userId;
        this.deletedAt = LocalDateTime.now();
    }

    public void recover() {
        this.deletedBy = null;
        this.deletedAt = null;
    }
}