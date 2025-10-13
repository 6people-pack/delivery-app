package com.sparta.delivery.ai.dto;

import com.sparta.delivery.ai.domain.Ai;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AiResponseDto {
    private String id;

    private String question;

    private String answer;

    private LocalDateTime createdAt;

//    private String createdBy;

    public AiResponseDto(Ai ai) {
        this.id = ai.getId().toString();
        this.question = ai.getQuestion();
        this.answer = ai.getAnswer();
        this.createdAt = ai.getCreatedAt();
    }
}
