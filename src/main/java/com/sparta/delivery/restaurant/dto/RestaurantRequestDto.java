package com.sparta.delivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record RestaurantRequestDto(
        @NotNull(message = "위도는 필수 입력값 입니다.")
        Double latitude,

        @NotNull(message = "경도는 필수 입력값 입니다.")
        Double longitude,

        @NotBlank(message = "주소는 필수 입력값 입니다.")
        String address,

        @NotBlank(message = "상세 주소는 필수 입력값 입니다.")
        String addressDetail,

        @NotBlank(message = "가게 전화 번호는 필수 입력값 입니다.")
        String phoneNumber,

        @NotBlank(message = "사업자 번호는 필수 입력값 입니다.")
        String businessNumber,

        @NotBlank(message = "가게 이름은 필수 입력값 입니다.")
        String name,

        @NotBlank(message = "가게 설명은 필수 입력값 입니다.")
        String description,

        @NotNull
        Integer minOrderPrice,

        @NotNull
        Boolean isOpen,

        @NotNull(message = "오픈 시간은 필수 입력값 입니다.")
        LocalTime openTime,

        @NotNull(message = "마감 시간은 필수 입력값 입니다.")
        LocalTime closeTime,

        @NotEmpty(message = "카테고리는 필수 입력값 입니다.")
        List<UUID> categories
) {}