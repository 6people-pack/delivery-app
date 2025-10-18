package com.sparta.delivery.ai.message;

import com.sparta.delivery.discord.message.DiscordEmbeddable;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class AiFailMessage implements DiscordEmbeddable {

    private final Long userId;
    private final String category;
    private final String categoryId;
    private final String reason;
    private final String errorMassage;

    @Override
    public String getTitle() {return "Gemini AI 설명 생성 실패 알림";}

    @Override
    public Map<String, String> getFields() {
        Map<String, String> fields = new LinkedHashMap<>();
        String userInfo = """
        **사용자 Id:** %s
        
        """.formatted(userId);
        fields.put("• 문의자 정보", userInfo);
        fields.put("\u200B", "\u200B");
        fields.put("• 카테고리", category);
        fields.put("• 카테고리 아이디", categoryId);
        fields.put("\u200C", "\u200C");
        fields.put("• 실패 사유", reason);
        fields.put("• 에러 메시지", "```" + errorMassage + "```");
        return fields;
    }
}
