package com.sparta.delivery.inquiry.listener;


import com.sparta.delivery.discord.api.DiscordService;
import com.sparta.delivery.inquiry.event.InquiryCreateEvent;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class InquiryAlamEventListener {

    private final DiscordService discordService;

    @Description("고객센터 문의가 접수 될 시 Discord 알람을 보낸다")
    @Async("discordExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onInquiryAddEvent(InquiryCreateEvent event) {
        discordService.InquirySendMessageToDiscord(
            event.getUser(),
            event.getInquiry()
        );
    }


}
