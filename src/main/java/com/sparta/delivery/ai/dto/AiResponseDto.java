package com.sparta.delivery.ai.dto;

import lombok.Getter;

@Getter
public class AiResponseDto {

    private String answer;

    public AiResponseDto(String answer) {
        this.answer = answer;
    }
}
