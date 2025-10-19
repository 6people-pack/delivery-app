package com.sparta.delivery.ai.event;

import java.util.UUID;

public record AiGenerateFailedEvent(
        Long userId,                   // 누가 요청했는지
        String category,
        String categoryId,             // 어떤 식당에 대한 요청인지
        String reason,
        String errorMessage// 실패 원인 (예: "timeout", "502 Bad Gateway")
) {
    public AiGenerateFailedEvent(Long userId, String category, UUID categoryId, String reason, String errorMessage) {
        this(userId, category, categoryId.toString(), reason, errorMessage);
    }
}