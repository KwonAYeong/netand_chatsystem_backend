package com.netand.chatsystem.common;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;
import java.time.ZoneId;

@MappedSuperclass
@Getter
public abstract class BaseTimeEntity {

    @Column(updatable = false)
    protected LocalDateTime createdAt;

    protected LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}