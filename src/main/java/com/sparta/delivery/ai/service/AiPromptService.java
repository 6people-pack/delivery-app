package com.sparta.delivery.ai.service;

import com.sparta.delivery.ai.dto.AiKeywordsRequestDto;
import com.sparta.delivery.ai.dto.AiSimpleResponseDto;
import com.sparta.delivery.ai.dto.MenuKeywords;
import com.sparta.delivery.ai.dto.RestaurantKeywords;
import com.sparta.delivery.global.category.CategoryCheck;
import com.sparta.delivery.global.category.ImageCategory;
import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiPromptService {
    private final AiApiService aiApiService;
    private final CategoryCheck categoryCheck;

    //see : 원래는 front에서 질문을 완성해서 보내줘야 하지만, 지금은 front 작업이 안되어있으므로 여기서 질문을 완성
    //Ai api 요청 부분과 작성부분을 분리하는 게 좋음(단일 책임 원칙, 같은 클래스 내에선 getAnswerFromAi()의 트랜잭션이 실행 불가)
    public AiSimpleResponseDto GetAiWithKeywords(Long userId, AiKeywordsRequestDto requestDto) {
        ImageCategory imageCategory = ImageCategory.valueOf(requestDto.getCategory());
        UUID categoryId = requestDto.getCategoryId();
        categoryCheck.checkAuthority(userId, imageCategory, categoryId);
        if(imageCategory.equals(ImageCategory.restaurant)) {
            RestaurantKeywords keywords = (RestaurantKeywords) requestDto.getKeywords();
            String question = "나는 음식점 사장이야. 내 음식점의 이름은 " + keywords.name() + " 이고, " +
                    "내 음식점의 대표 메뉴는 " + keywords.mainDish() + " 야. " +
                    "내 음식점의 장점은 " + keywords.advantage() + " 이지. " +
                    keywords.highlight() + "한다는 점을 중점으로 이 음식점을 홍보하기 위한 한줄 설명 문구를 만들어줘";
            return aiApiService.generateAiAnswer(userId, requestDto.getCategory(), requestDto.getCategoryId(), question);
        } else if (ImageCategory.valueOf(requestDto.getCategory()).equals(ImageCategory.menu)) {
            MenuKeywords keywords = (MenuKeywords) requestDto.getKeywords();
            String question = "나는 음식점 사장이야. 이 메뉴의 이름은 " + keywords.name() + " 이고,  " +
                    "음식 종류는 " + keywords.category() + " 이고. " +
                    "주재료는 " + keywords.mainIngredient() + " 이야, " +
                    keywords.highlight() + "한다는 점을 중점으로 이 음식점을 홍보하기 위한 한줄 설명 문구를 만들어줘";
            return aiApiService.generateAiAnswer(userId, requestDto.getCategory(), requestDto.getCategoryId(), question);
        } else {
            throw new BusinessException(ErrorCode.NO_USE_CATEGORY);
        }
    }

}
