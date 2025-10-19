package com.sparta.delivery.restaurant.dto;

import com.sparta.delivery.restaurant.domain.ApprovalStatus;
import lombok.Builder;

import java.time.LocalTime;
import java.util.UUID;

@Builder
public record RestaurantDetailResponseDto(
        UUID restaurantId,
        String address,
        String addressDetail,
        String phoneNumber,
        String businessNumber,
        String name,
        String description,
        int minOrderPrice,
        Boolean isOpen,
        LocalTime openTime,
        LocalTime closeTime,
        Double rating,
        ApprovalStatus approvalStatus
) {
}
