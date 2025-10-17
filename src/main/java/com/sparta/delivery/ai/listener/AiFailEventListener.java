package com.sparta.delivery.ai.listener;

import com.sparta.delivery.ai.event.AiGenerateFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiFailEventListener {

    //비동기적으로 관리자 알림기능
    @EventListener(AiGenerateFailedEvent.class)
    public void handleAiGenerateFailedEvent(AiGenerateFailedEvent event) {
        //로그를 남기거나 알림을 보내는 등의 추가 작업 필요
        log.error("Ai 생성 실패 이벤트 발생, userId: {}, category: {}, categoryId: {}, question: {}, attempt: {}, error: {}",
                event.userId(), event.category(), event.categoryId(), event.question(), event.attempt(), event.getClass().getSimpleName());
    }
}
