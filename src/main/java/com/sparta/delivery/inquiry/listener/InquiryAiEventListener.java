package com.sparta.delivery.inquiry.listener;

import com.sparta.delivery.inquiry.event.InquiryCreateEvent;
import com.sparta.delivery.inquiry.service.AiResponder;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class InquiryAiEventListener {

    private final AiResponder aiResponder;

    @Async("aiExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retry(name = "ai", fallbackMethod = "fallbackToDlq")
    public void onInquiryCreatedForAi(InquiryCreateEvent event) {
        aiResponder.aiAnswerComment(event.getUser(), event.getInquiry());
    }


    // 재시도 소진 시 DLQ로 폴백
    private void fallbackToDlq(InquiryCreateEvent event, Throwable cause) {
        aiResponder.sendToDlq(event.getInquiry().getId(), cause);
        log.warn("AI fallback → DLQ. inquiryId={}, cause={}", event.getInquiry().getId(), cause == null ? "unknown" : cause.toString());
    }

}
