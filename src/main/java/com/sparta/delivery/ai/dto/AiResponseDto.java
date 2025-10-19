package com.sparta.delivery.ai.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AiResponseDto(UUID id,
                            String question,
                            String answer,
                            LocalDateTime createdAt) {
    //    private String createdBy;


}
