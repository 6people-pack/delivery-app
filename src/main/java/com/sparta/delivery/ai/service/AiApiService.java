package com.sparta.delivery.ai.service;

import com.sparta.delivery.ai.dto.AiSimpleResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;


@Slf4j
@Service

public class AiApiService {

    private final RestTemplate restTemplate;
    private final AiService aiService;

    public AiApiService(RestTemplateBuilder builder, AiService aiService) {
        this.restTemplate = builder.build();
        this.aiService = aiService;
    }

    //todo : api key 숨기기
    //todo : 응답 안받았을때 대응 필요
    public AiSimpleResponseDto getAnswerFromAi(String question) {
        //요청 url 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://generativelanguage.googleapis.com")
                .path("/v1beta/models/gemini-2.5-flash:generateContent")
                .encode()
                .build()
                .toUri();
        log.info("uri = " + uri);
        RequestEntity<String> requestEntity = RequestEntity
                .post(uri)
                .header("x-goog-api-key","AIzaSyC6oQVtyAm7afSkGVlBq-XYr73ZEfjydNg")
                .header("Content-Type","application/json")
                .body(fromQuestiontoJSON(question));

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
