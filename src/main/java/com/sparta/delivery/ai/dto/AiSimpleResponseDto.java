package com.sparta.delivery.ai.dto;

import lombok.Getter;

@Getter
public class AiSimpleResponseDto {
    private String answer;

    public AiSimpleResponseDto(String answer) {
        this.answer = answer;
    }
}
