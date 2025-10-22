package com.sparta.delivery.restaurant.message;


import com.sparta.delivery.discord.message.DiscordEmbeddable;
import com.sparta.delivery.restaurant.domain.Restaurant;
import com.sparta.delivery.user.domain.User;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestaurantMessage implements DiscordEmbeddable {

    private final User user;
    private final Restaurant restaurant;

    @Override
    public String getTitle() {
        return "식당 등록 요청";
    }

    @Override
    public Map<String, String> getFields() {
        Map<String, String> fields = new LinkedHashMap<>();

        String userInfo = """
        **사용자 닉네임:** %s
        
        """.formatted(user.getNickname());   // TODO   """.formatted(user.getName(), user.getNickname()); 본명 필요해 보임
        fields.put("• 문의자 정보", userInfo);
        fields.put("\u200B", "\u200B");
        fields.put("• 식당 이름", restaurant.getName());
        fields.put("• 식당 주소", "```" + restaurant.getAddress() + "```");

        return fields;
    }
}
