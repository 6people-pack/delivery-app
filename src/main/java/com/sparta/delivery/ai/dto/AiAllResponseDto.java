package com.sparta.delivery.ai.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class AiAllResponseDto {
    private List<AiResponseDto> aiResponses;

    public AiAllResponseDto(List<AiResponseDto> aiResponses) {
        this.aiResponses = aiResponses;
    }
}
