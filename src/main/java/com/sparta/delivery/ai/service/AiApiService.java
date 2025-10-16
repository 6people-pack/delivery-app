package com.sparta.delivery.ai.service;

import com.querydsl.core.BooleanBuilder;
import com.sparta.delivery.ai.domain.Ai;
import com.sparta.delivery.ai.domain.QAi;
import com.sparta.delivery.ai.dto.*;
import com.sparta.delivery.ai.repository.AiRepository;
import com.sparta.delivery.global.category.CategoryCheck;
import com.sparta.delivery.global.category.ImageCategory;
import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.user.domain.Role;
import com.sparta.delivery.user.domain.User;
import com.sparta.delivery.user.repository.UserRepository;
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

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service

public class AiApiService {

    private final RestTemplate restTemplate;
    private final AiRepository aiRepository;
    private final UserRepository userRepository;
    private final CategoryCheck categoryCheck;

    public AiApiService(RestTemplateBuilder builder, AiRepository aiRepository, UserRepository userRepository, CategoryCheck categoryCheck) {
        this.restTemplate = builder.build();
        this.aiRepository = aiRepository;
        this.userRepository = userRepository;
        this.categoryCheck = categoryCheck;
    }
    public AiSimpleResponseDto GetAiWithKeywords(Long userId, AiKeywordsRequestDto requestDto) {
        ImageCategory imageCategory = ImageCategory.valueOf(requestDto.getCategory());
        UUID categoryId = UUID.fromString(requestDto.getCategoryId());
        categoryCheck.checkAuthority(userId, imageCategory, categoryId);
        if(imageCategory.equals(ImageCategory.restaurant)) {
            RestaurantKeywords keywords = (RestaurantKeywords) requestDto.getKeywords();
            String question = "나는 음식점 사장이야. 내 음식점의 이름은 " + keywords.name() + " 이고, " +
                    "내 음식점의 대표 메뉴는 " + keywords.mainDish() + " 야. " +
                    "내 음식점의 장점은 " + keywords.advantage() + " 이지. " +
                    keywords.highlight() + "한다는 점을 중점으로 이 음식점을 홍보하기 위한 한줄 설명 문구를 만들어줘";
            return getAnswerFromAi(question);
        } else if (ImageCategory.valueOf(requestDto.getCategory()).equals(ImageCategory.menu)) {
            MenuKeywords keywords = (MenuKeywords) requestDto.getKeywords();
            String question = "나는 음식점 사장이야. 이 메뉴의 이름은 " + keywords.name() + " 이고,  " +
                    "음식 종류는 " + keywords.category() + " 이고. " +
                    "주재료는 " + keywords.mainIngredient() + " 이야, " +
                    keywords.highlight() + "한다는 점을 중점으로 이 음식점을 홍보하기 위한 한줄 설명 문구를 만들어줘";
            return getAnswerFromAi(question);
        } else {
            throw new BusinessException(ErrorCode.NO_USE_CATEGORY);
        }
    }


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
        log.info("AI API Question : " + question);
        String answer = fromJSONtoAnswer(responseEntity.getBody());
        log.info("AI API Answer : " + answer);

        Ai ai = new Ai(question, answer);
        aiRepository.save(ai);
        return new AiSimpleResponseDto(answer);
    }


    // 모든 질문과 답변 조회
    @Transactional(readOnly = true)
    public AiAllResponseDto getAllChats(Long userId, String startday, String endday, String word) {
        //권한 체크
        User user = userRepository.findById(userId).orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if(user.getRole() != Role.ADMIN) throw new BusinessException(ErrorCode.NOT_ADMIN);

        //검색 조건 빌더
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

    @Transactional(readOnly = true)
    public AiResponseDto getChat(Long userId, String aiId) {
        //권한 체크
        User user = userRepository.findById(userId).orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if(user.getRole() != Role.ADMIN) throw new BusinessException(ErrorCode.NOT_ADMIN);

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
