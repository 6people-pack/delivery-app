package com.sparta.delivery.ai.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Entity
@Table(name="")
@NoArgsConstructor
public class Ai {

    @Id
    private UUID id;

    @Column(nullable = false, length = 500)
    private String question;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String answer;

    public Ai(String question, String answer) {
        this.id = UUID.randomUUID();
        this.question = question;
        this.answer = answer;
    }
}
