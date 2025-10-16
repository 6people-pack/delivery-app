package com.sparta.delivery.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 카테고리별 키워드 DTO
public record RestaurantKeywords(
        @NotBlank(message = "이름은 필수 입력값입니다.")
        String name,     // 가게 이름
        @NotBlank (message = "대표 메뉴는 필수 입력값입니다.")
        String mainDish,     // 대표 메뉴
        @NotBlank(message = "장점은 필수 입력값입니다.")
        String advantage,       // 장점
        @Size(message = "강조하는 내용은 필수 입력값입니다.",max = 100)
        String highlight  //중점
) implements Keywords { }
