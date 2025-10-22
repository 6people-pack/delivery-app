package com.sparta.delivery.ai.service;

import com.sparta.delivery.ai.dto.AiAnswerResponseDto;
import com.sparta.delivery.ai.dto.AiSimpleResponseDto;
import com.sparta.delivery.ai.event.AiGenerateFailedEvent;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;
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


    @Retry(name="ai", fallbackMethod = "recoverGenerateAiAnswer")
    public AiAnswerResponseDto generateAiAnswer(Long userId, String category, UUID categoryId, String question) {
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
        List<String> answers = fromJSONtoAnswer(responseEntity.getBody());
        DateTimeFormatter timeformat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        log.warn("AI 요청 - userId : {}, category : {}, categoryId : {}, question : {}, answer : {}, timestamp: {}", userId, category, categoryId, question,answers, LocalDateTime.now().format(timeformat));

        aiService.saveAi(question, answers.toString().replaceFirst("^\\[", "").replaceFirst("\\]$", ""));
        return new AiAnswerResponseDto(answers.stream().map(AiSimpleResponseDto::new).toList());
    }

    //최종 실패시 discord 알림 및 로그 남기기
    public AiAnswerResponseDto recoverGenerateAiAnswer(Long userId, String category, UUID categoryId, String question, Throwable e) {
        String errorMessage = e.getMessage().substring(e.getMessage().lastIndexOf("message")+10);
        errorMessage = errorMessage.substring(0,errorMessage.indexOf("<EOL>"));
        log.error("AI 재시도 최종 실패 - userId : {}, category : {}, categoryId : {}, question : {}, occurredAt: {}, error: {}", userId, category, categoryId, question, LocalDateTime.now(), errorMessage);
        publisher.publishEvent(new AiGenerateFailedEvent(userId, category, categoryId,  e.getClass().getSimpleName(), errorMessage));
        return new AiAnswerResponseDto(List.of(new AiSimpleResponseDto("AI 응답이 없습니다. 잠시 후 다시 시도해주세요.")));
    }

    // 질문을 json 형태로 변환
    private String fromQuestiontoJSON(String question) {
        //질문 넣기
        JSONObject root = new JSONObject();

        // contents -> [ { parts: [ { text: "..." } ] } ]
        JSONArray contents = new JSONArray();
        JSONObject contentObj = new JSONObject();
        JSONArray parts = new JSONArray();
        JSONObject part = new JSONObject();
        part.put("text", question.replace("\"", "\\\""));
        parts.put(part);
        contentObj.put("parts", parts);
        contents.put(contentObj);
        root.put("contents", contents);

        // generationConfig 추가
        JSONObject generationConfig = new JSONObject();
        generationConfig.put("temperature", 0.8);
        generationConfig.put("topK", 10);
        generationConfig.put("candidateCount", 3);
        root.put("generationConfig", generationConfig);

        return root.toString();
    }

    //응답에서 answer 추출
    private List<String> fromJSONtoAnswer(String responseBody) {
        JSONObject response = new JSONObject(responseBody);
        JSONArray candidates = response.getJSONArray("candidates");
        List<String> answers = new ArrayList<>();
        for(Object content : candidates)  {
            String text = ((JSONObject)content).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
            text = text.substring(text.indexOf(":")+1);  //대답 부분만 가져오기
            text = text.replaceAll("\\*", "").replaceAll("\"", "");  //강조체를 위한 **, 쌍따옴표 제거
            text = text.stripLeading(); //선행 공백 제거
            answers.add(text);
        }
        return answers;
    }


}
