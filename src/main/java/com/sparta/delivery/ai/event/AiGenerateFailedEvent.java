package com.sparta.delivery.ai.event;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record AiGenerateFailedEvent(
        Long userId,                   // 누가 요청했는지
        String category,
        UUID categoryId,             // 어떤 식당에 대한 요청인지
        String question,               // AI에게 보낸 질문 (요약 생성문)
//        Map<String, Object> params,    // AI 모델 파라미터(온도, max_tokens 등)
        int attempt,                   // 현재까지 몇 번째 시도인지
        String reason,                 // 실패 원인 (예: "timeout", "502 Bad Gateway")
        UUID eventId,                  // 추적용 UUID
        LocalDateTime occurredAt             // 이벤트 발생 시각
) {
    public AiGenerateFailedEvent(Long userId, String category, UUID categoryId, String question, int attempt, String reason) {
        this(userId, category, categoryId, question, attempt, reason, UUID.randomUUID(), LocalDateTime.now());
    }
}