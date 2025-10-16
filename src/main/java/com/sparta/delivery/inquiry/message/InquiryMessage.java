package com.sparta.delivery.inquiry.message;


import com.sparta.delivery.discord.message.DiscordEmbeddable;
import com.sparta.delivery.user.domain.User;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InquiryMessage implements DiscordEmbeddable {

    private final User user;
    private final String title;
    private final String content;



    @Override
    public String getTitle() {
        return "고객센터 문의 내용";
    }
    

    @Override
    public Map<String, String> getFields() {
        Map<String, String> fields = new LinkedHashMap<>();

        String userInfo = """
        **사용자 닉네임:** %s
        
        """.formatted(user.getNickname());   // TODO   """.formatted(user.getName(), user.getNickname()); 본명 필요해 보임
        fields.put("• 문의자 정보", userInfo);
        fields.put("\u200B", "\u200B");
        fields.put("• 문의 제목", title);
        fields.put("• 문의 내용", "```" + content + "```");

        // 해당 게시물로 가는 링크 넣어도 괜찮을 듯

        return fields;
    }
}
