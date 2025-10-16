package com.sparta.delivery.restaurant.dto;


import com.sparta.delivery.restaurant.domain.RatingStatus;
import jakarta.validation.constraints.NotNull;

public record RatingRequestDto (
        @NotNull(message = "상태(삭제, 추가)는 필수 입력값 입니다.")
        RatingStatus status,
        @NotNull(message = "별점은 필수 입력값 입니다.")
        Double rating
) {}
