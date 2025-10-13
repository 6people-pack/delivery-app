package com.sparta.delivery.ai.service;

import com.sparta.delivery.ai.repository.AiRepository;
import com.sparta.delivery.ai.domain.Ai;
import com.sparta.delivery.ai.dto.AiResponseDto;
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
    private final AiRepository aiRepository;

    public AiApiService(RestTemplateBuilder builder, AiRepository aiRepository) {
        this.aiRepository = aiRepository;
        this.restTemplate = builder.build();
    }

    public AiResponseDto getAnswerFromAi(String question) {

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
        log.info("AI API Answer : " + answerTest(responseEntity.getBody()));
        String answer = fromJSONtoAnswer(responseEntity.getBody());

        Ai ai = new Ai(question, answer);
        aiRepository.save(ai);


        return new AiResponseDto(answer);
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


    private String answerTest(String responseBody) {
        JSONObject response = new JSONObject(responseBody);
        return response.getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
    }
    //응답에서 answer 추출
    private String fromJSONtoAnswer(String responseBody) {
        JSONObject response = new JSONObject(responseBody);
        String text = response.getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
        return text.substring(text.indexOf(":")+3,text.length()-2);
    }
}
