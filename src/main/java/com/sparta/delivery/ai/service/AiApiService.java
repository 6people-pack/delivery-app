package com.sparta.delivery.ai.service;

import com.sparta.delivery.ai.dto.AiSimpleResponseDto;
import com.sparta.delivery.ai.event.AiGenerateFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
import java.util.UUID;


@Slf4j
@Service

public class AiApiService {

    private final RestTemplate restTemplate;
    private final AiService aiService;
    private final ApplicationEventPublisher publisher;

    private final int MAX_RETRY = 3;

    public AiApiService(RestTemplateBuilder builder, AiService aiService, ApplicationEventPublisher publisher) {
        this.restTemplate = builder.build();
        this.aiService = aiService;
        this.publisher = publisher;
    }

    @Value("${gemini.api-key}")
    private String apikey;


    //todo : 응답 안받았을때 대응 필요 - 후에 @retry 사용
    //todo : 다른 dto도 id를 UUID로 바꿈
    //todo : error message 파싱 개선
    public AiSimpleResponseDto generateAiAnswer(Long userId, String category, UUID categoryId, String question) {
        AiSimpleResponseDto response = null;
        try{
            response =  getAnswerFromGemini(question);
        }catch (Exception e){
            int attempt = 0;
            while(attempt < MAX_RETRY){
                String errorMessage;
                try{
                    attempt++;
                    errorMessage = e.getMessage().substring(e.getMessage().lastIndexOf("message")+10);
                    errorMessage = errorMessage.substring(0,errorMessage.indexOf("<EOL>"));
                    log.warn("AI 응답 실패 및 재시도 - userId : {}, category : {}, categoryId : {}, question : {}, attempt : {}, error: {}", userId, category, categoryId, question, attempt, errorMessage);
                    response = getAnswerFromGemini(question);
                    break;
                }catch (Exception ex){
                    if(attempt >=MAX_RETRY) {
                        errorMessage = ex.getMessage().substring(ex.getMessage().lastIndexOf("message")+10);
                        errorMessage = errorMessage.substring(0,errorMessage.indexOf("<EOL>"));
                        log.error("AI 재시도 최종 실패 - userId : {}, category : {}, categoryId : {}, question : {}, occurredAt: {}, error: {}", userId, category, categoryId, question, LocalDateTime.now(), errorMessage);
                        response = new AiSimpleResponseDto("AI 응답이 없습니다. 잠시 후 다시 시도해주세요.");
                        publisher.publishEvent(new AiGenerateFailedEvent(userId, category, categoryId,  ex.getClass().getSimpleName(), errorMessage));
                    }
                    try {
                        Thread.sleep(2000);  //재시도 간격 2초
                    }catch (InterruptedException exc){
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        return response;
    }

    @NotNull
    private AiSimpleResponseDto getAnswerFromGemini(String question) {
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
        log.info("AI API Question : " + question);
        String answer = fromJSONtoAnswer(responseEntity.getBody());
        log.info("AI API Answer : " + answer);

        aiService.saveAi(question, answer);
        return new AiSimpleResponseDto(answer);
    }

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
