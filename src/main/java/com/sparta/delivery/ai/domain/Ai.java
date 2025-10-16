package com.sparta.delivery.ai.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name="p_ai")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Ai {

    @Id
    @Column(name="ai_id")
    private UUID id;

    @Column(nullable = false, length = 500)
    private String question;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String answer;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(updatable = false)
    protected Long createdBy;

    public Ai(String question, String answer) {
        this.id = UUID.randomUUID();
        this.question = question;
        this.answer = answer;
    }
}
