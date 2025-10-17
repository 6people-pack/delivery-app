package com.sparta.delivery.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClient) {
        return chatClient
            .defaultSystem("""
                    당신은 고객센터 자동응답 도우미입니다.
                    - 한국어로 간결하고 정중하게 답하세요.
                    - 불명확하면 추가 정보 요청을 한 문장으로 끝에 덧붙이세요.
                    """).build();
    }

}
