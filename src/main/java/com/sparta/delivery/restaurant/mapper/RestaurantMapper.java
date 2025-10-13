package com.sparta.delivery.restaurant.mapper;

import com.sparta.delivery.restaurant.domain.Restaurant;
import com.sparta.delivery.restaurant.dto.RestaurantRequestDto;

public class RestaurantMapper {
    public static Restaurant toRestaurant(Long userId, RestaurantRequestDto requestDto, String description) {
        return Restaurant.builder()
                .ownerId(userId)
                .name(requestDto.name())
                .latitude(requestDto.latitude())
                .longitude(requestDto.longitude())
                .address(requestDto.address())
                .addressDetail(requestDto.addressDetail())
                .phoneNumber(requestDto.phoneNumber())
                .businessNumber(requestDto.businessNumber())
                .description(description) // Todo : AI 통한 설명 생성후 저장
                .minOrderPrice(requestDto.minOrderPrice())
                .isOpen(requestDto.isOpen())
                .openTime(requestDto.openTime())
                .closeTime(requestDto.closeTime())
                .build();
    }
}
