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


    // 재시도 소진 시 DLQ로 폴백 (실패 이벤트 보관소 느낌으로 나중에 수동적으로 재처리 하거나 자동 복구 스케줄러가 일정 간격으로 재시도 가능하게 구현하는 듯)
    private void fallbackToDlq(InquiryCreateEvent event, Throwable cause) {
        aiResponder.sendToDlq(event.getInquiry().getId(), cause);
        log.warn("AI fallback → DLQ. inquiryId={}, cause={}", event.getInquiry().getId(), cause == null ? "unknown" : cause.toString());
    }

}
