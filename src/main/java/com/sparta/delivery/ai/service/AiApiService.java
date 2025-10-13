package com.sparta.delivery.ai.service;

import com.querydsl.core.BooleanBuilder;
import com.sparta.delivery.ai.domain.Ai;
import com.sparta.delivery.ai.domain.QAi;
import com.sparta.delivery.ai.dto.AiAllResponseDto;
import com.sparta.delivery.ai.dto.AiResponseDto;
import com.sparta.delivery.ai.dto.AiSimpleResponseDto;
import com.sparta.delivery.ai.repository.AiRepository;
import com.sparta.delivery.global.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.sparta.delivery.global.exception.domain.ErrorCode;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@Transactional(readOnly = true)
public class AiApiService {

    private final RestTemplate restTemplate;
    private final AiRepository aiRepository;

    public AiApiService(RestTemplateBuilder builder, AiRepository aiRepository) {
        this.aiRepository = aiRepository;
        this.restTemplate = builder.build();
    }


    //todo : 입력 텍스트의 글자수를 제한, 실제 요청 텍스트 마지막에 “답변을 최대한 간결하게 50자 이하로 작성해줘" 추가
    @Transactional
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
        log.info("AI API Answer : " + answerTest(responseEntity.getBody()));
        String answer = fromJSONtoAnswer(responseEntity.getBody());

        Ai ai = new Ai(question, answer);
        aiRepository.save(ai);
        return new AiSimpleResponseDto(answer);
    }

    // 모든 질문과 답변 조회
    //todo : 검색 기록은 관리자만 가능하도록 권한 추가
    public AiAllResponseDto getAllChats(String startday, String endday, String word) {

            BooleanBuilder builder  = new BooleanBuilder();
            QAi ai = QAi.ai;

            if(StringUtils.hasText(startday)) {
                LocalDateTime sDay = LocalDateTime.of(LocalDate.parse(startday), LocalTime.of(0,0,0));
                builder.and(ai.createdAt.goe(sDay));
            }

            if(StringUtils.hasText(endday)) {
                LocalDateTime eDay = LocalDateTime.of(LocalDate.parse(endday), LocalTime.of(23,59,59));
                builder.and(ai.createdAt.loe(eDay));
            }

            if(StringUtils.hasText(word)) {
                builder.and(ai.answer.contains(word));
            }

        List<Ai> aiList = (List<Ai>) aiRepository.findAll(builder, Sort.by(Sort.Order.desc("createdAt")));

        List<AiResponseDto> aiResponseDtoList = aiList.stream()
                .map(AiResponseDto::new)
                .toList();
        return new AiAllResponseDto(aiResponseDtoList);
    }

    public AiResponseDto getChat(String aiId) {
        Ai ai = aiRepository.findById(UUID.fromString(aiId)).orElseThrow(()-> new BusinessException(ErrorCode.AI_NOT_FOUND));
        return new AiResponseDto(ai);
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
        text = text.substring(text.indexOf(":")+1,text.length()-2);
        //:이후에 공백이 있으면 2칸 제거, 없으면 1칸 제거
        if(text.startsWith(" ")) return text.substring(2);
        return text.substring(1);
    }


}
