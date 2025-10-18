package com.sparta.delivery.ai.dto;

import lombok.Getter;

@Getter
public class AiSimpleResponseDto {
    private final String answer;

    public AiSimpleResponseDto(String answer) {
        this.answer = answer;
    }
}
