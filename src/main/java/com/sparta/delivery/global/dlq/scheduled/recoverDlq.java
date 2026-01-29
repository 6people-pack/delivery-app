package com.sparta.delivery.global.dlq.scheduled;

import com.sparta.delivery.global.dlq.domain.DlqMessage;
import com.sparta.delivery.global.dlq.domain.DlqStatus;
import com.sparta.delivery.global.dlq.repository.DlqMessageRepository;
import com.sparta.delivery.global.dlq.service.RecoveryService;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class recoverDlq {

    private static final int BATCH_SIZE = 50; // 실패 작업을 한번에 몇 개씩 재시도 할지
    private static final int MAX_ATTEMPTS = 3; // 3번 하고도 실패면 종결

    private final DlqMessageRepository dlqMessageRepository;
    private final RecoveryService recoveryService;

    // ⏱ 매 1분마다 실행 (Asia/Seoul)
    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    @Transactional
    public void recoverDlq() {
        LocalDateTime now = LocalDateTime.now();

        // 1) 지금 처리할 차례가 된 것들만 잠그며 소량 집기
        List<DlqMessage> dlqMessages = dlqMessageRepository.pendingDlqLock(DlqStatus.PENDING, MAX_ATTEMPTS, now, PageRequest.of(0, BATCH_SIZE));
        if (dlqMessages.isEmpty()) return;

        // 2) 집자마자 PROCESSING으로 전이 (중복 집기 차단)
        List<UUID> ids = dlqMessages.stream().map(DlqMessage::getId).toList();
        int changed = dlqMessageRepository.changeStatus(ids, DlqStatus.PENDING, DlqStatus.PROCESSING);
        if (changed == 0) return;

        // 3) 각 메시지 처리
        for (DlqMessage dlqMessage : dlqMessages) {
            try {
                recoveryService.reprocessOnce(dlqMessage.getInquiryId());
                dlqMessage.updateDlqStatus(DlqStatus.SUCCESS);
            } catch (Exception e) {
                // 위에서 재시도 처리를 하고 성공하면 냅두지만 실패 시 Exception이 발생해 로직들 수행
                int retryCount = dlqMessage.getRetryCount() + 1;
                dlqMessage.retryUpCount();

                if (retryCount >= MAX_ATTEMPTS) {
                    dlqMessage.updateDlqStatus(DlqStatus.FAILED); // 그냥 실패로 처리하고 더는 재처리 하지 않음
                    // TODO 관리자에게 알림 추가 예정
                }
                else {
                    dlqMessage.updateDlqStatus(DlqStatus.PENDING);
                    dlqMessage.updateNextRunTime(now.plus(backoff(retryCount)));
                }
            }
            
        }

    }

    private Duration backoff(int retryCount) {
        return switch (retryCount) {
            case 1 -> Duration.ofMinutes(1);   // 1번째 실패 후: +1분
            case 2 -> Duration.ofMinutes(5);   // 2번째 실패 후: +5분
            default -> Duration.ofMinutes(30); // 3번째 실패 후: +30분 (다음이 마지막 시도)
        };
    }

}
