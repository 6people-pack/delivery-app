package com.sparta.delivery.ai.listener;

import com.sparta.delivery.ai.event.AiGenerateFailedEvent;
import com.sparta.delivery.discord.api.DiscordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiFailEventListener {

    private final DiscordService discordService;

    //비동기적으로 관리자 알림기능
    @Async
    @EventListener(AiGenerateFailedEvent.class)
    public void handleAiGenerateFailedEvent(AiGenerateFailedEvent event) {
        //로그를 남기거나 알림을 보내는 등의 추가 작업 필요
        discordService.AiFailSendMessageToDiscord(event.userId(),
                event.category(),
                event.categoryId(),
                event.reason(),
                event.errorMessage());
    }
}
