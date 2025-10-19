package com.sparta.delivery.restaurant.listener;

import com.sparta.delivery.discord.api.DiscordService;
import com.sparta.delivery.restaurant.event.RestaurantCreateEvent;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RestaurantAlamEventListener {

    private final DiscordService discordService;

    @Description("식당 등록 요청 시 Discord 알람을 보낸다")
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreateAddEvent(RestaurantCreateEvent event) {
        discordService.RestaurantOwnerSendMessageToDiscord(
            event.getUser(),
            event.getRestaurant()
        );
    }
}
