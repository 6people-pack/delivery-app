package com.sparta.delivery.ai.service;

import com.sparta.delivery.ai.dto.AiSimpleResponseDto;
import com.sparta.delivery.ai.event.AiGenerateFailedEvent;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


@Slf4j
@Service

public class AiApiService {

    private final RestTemplate restTemplate;
    private final AiService aiService;
    private final ApplicationEventPublisher publisher;

    public AiApiService(RestTemplateBuilder builder, AiService aiService, ApplicationEventPublisher publisher) {
        this.restTemplate = builder.build();
        this.aiService = aiService;
        this.publisher = publisher;
    }

    @Value("${gemini.api-key}")
    private String apikey;


    //todo : 다른 dto도 id를 UUID로 바꿈
    @Retry(name="ai", fallbackMethod = "recoverGenerateAiAnswer")
    public AiSimpleResponseDto generateAiAnswer(Long userId, String category, UUID categoryId, String question) {
        //요청 url 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://generativelanguage.googleapis.com")
                .path("/v1beta/models/gemini-2.5-flash:generateContent")
                .encode()
                .build()
                .toUri();

        //요청 만들기
        RequestEntity<String> requestEntity = RequestEntity
                .post(uri)
                .header("x-goog-api-key",apikey)
                .header("Content-Type","application/json")
                .body(fromQuestiontoJSON(question));

        //요청 보내기
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        log.info("AI API Status Code : " + responseEntity.getStatusCode());
        String answer = fromJSONtoAnswer(responseEntity.getBody());
        DateTimeFormatter timeformat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        log.warn("AI 요청 - userId : {}, category : {}, categoryId : {}, question : {}, answer : {}, timestamp: {}", userId, category, categoryId, question,answer, LocalDateTime.now().format(timeformat));

        aiService.saveAi(question, answer);
        return new AiSimpleResponseDto(answer);
    }

    //최종 실패시 discord 알림 및 로그 남기기
    public AiSimpleResponseDto recoverGenerateAiAnswer(Long userId, String category, UUID categoryId, String question, Throwable e) {
        String errorMessage = e.getMessage().substring(e.getMessage().lastIndexOf("message")+10);
        errorMessage = errorMessage.substring(0,errorMessage.indexOf("<EOL>"));
        log.error("AI 재시도 최종 실패 - userId : {}, category : {}, categoryId : {}, question : {}, occurredAt: {}, error: {}", userId, category, categoryId, question, LocalDateTime.now(), errorMessage);
        publisher.publishEvent(new AiGenerateFailedEvent(userId, category, categoryId,  e.getClass().getSimpleName(), errorMessage));
        return new AiSimpleResponseDto("AI 응답이 없습니다. 잠시 후 다시 시도해주세요.");
    }

//    @NotNull
//    private AiSimpleResponseDto getAnswerFromGemini(String question) {
//        //요청 url 만들기
//        URI uri = UriComponentsBuilder
//                .fromUriString("https://generativelanguage.googleapis.com")
//                .path("/v1beta/models/gemini-2.5-flash:generateContent")
//                .encode()
//                .build()
//                .toUri();
//
//        //요청 만들기
//        RequestEntity<String> requestEntity = RequestEntity
//                .post(uri)
//                .header("x-goog-api-key",apikey)
//                .header("Content-Type","application/json")
//                .body(fromQuestiontoJSON(question));
//
//        //요청 보내기
//        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
//        log.info("AI API Status Code : " + responseEntity.getStatusCode());
//        log.info("AI 요청 - userId : {}, category : {}, categoryId : {}, question : {}", userId, category, categoryId, question);
//        String answer = fromJSONtoAnswer(responseEntity.getBody());
//        log.warn("AI API Answer : " + answer);
//
//        aiService.saveAi(question, answer);
//        return new AiSimpleResponseDto(answer);
//    }

    // 질문을 json 형태로 변환
    private String fromQuestiontoJSON(String question) {
        return  String.format("""
            {
              "contents": [
                {
                  "parts": [
                    {
                      "text": "%s"
                    }
                  ]
                }
              ],
              "generationConfig": {
                "responseMimeType":"application/json",
                "responseSchema": {
                  "type": "OBJECT",
                  "properties": {
                    "answer": {"type": "string"}
                  }
                }
               }
            }
            """,  question.replace("\"", "\\\""));
    }

    //응답에서 answer 추출
    private String fromJSONtoAnswer(String responseBody) {
        JSONObject response = new JSONObject(responseBody);
        String text = response.getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
        text = text.substring(text.indexOf(":")+1,text.length()-2);
        //:이후에 공백이 있으면 2칸 제거, 없으면 1칸 제거
        if(text.startsWith(" ")) return text.substring(2);
        return text.substring(1);
    }


}
