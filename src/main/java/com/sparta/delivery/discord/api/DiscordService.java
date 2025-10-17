package com.sparta.delivery.discord.api;


import com.sparta.delivery.discord.message.DiscordMessageConverter;
import com.sparta.delivery.inquiry.domain.Inquiry;
import com.sparta.delivery.inquiry.message.InquiryMessage;
import com.sparta.delivery.restaurant.domain.Restaurant;
import com.sparta.delivery.restaurant.message.RestaurantMessage;
import com.sparta.delivery.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@Slf4j
public class DiscordService {


    private final JDA jda;

    @Value("${discord.bot.inquiryChannel}")
    private String inquiryChannelId;

    @Value("${discord.bot.restaurantChannel}")
    private String restaurantChannelId;

    @Value("${discord.bot.aiChannel}")
    private String aiChannelId;


    // 사용자 문의 요청
    public void InquirySendMessageToDiscord(User user, Inquiry inquiry) {
        log.info("Inquiry Send Message To Discord");
        TextChannel channel = jda.getTextChannelById(inquiryChannelId);
        if (channel == null) {
            log.info("Channel not found");
        }
        MessageEmbed buildEmbedReportMessage = DiscordMessageConverter.buildReportMessage(new InquiryMessage(user, inquiry.getTitle(), inquiry.getContent()));
        channel.sendMessageEmbeds(buildEmbedReportMessage).queue();
    }

    //식당 사장의 식당 등록 요청
    public void RestaurantOwnerSendMessageToDiscord(User user, Restaurant restaurant) {
        log.info("Restaurant Owner Send Message To Discord");
        TextChannel channel = jda.getTextChannelById(restaurantChannelId);
        MessageEmbed buildEmbedReportMessage = DiscordMessageConverter.buildReportMessage(new RestaurantMessage(user, restaurant));
        channel.sendMessageEmbeds(buildEmbedReportMessage).queue();

    }
}






