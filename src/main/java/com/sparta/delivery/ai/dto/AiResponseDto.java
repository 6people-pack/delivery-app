package com.sparta.delivery.ai.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AiResponseDto {
    private String id;

    private String answer;

    private LocalDateTime createdAt;

//    private String createdBy;

    public AiResponseDto(String id, String answer, LocalDateTime createdAt) {
        this.id = id;
        this.answer = answer;
        this.createdAt = createdAt;
    }
}
