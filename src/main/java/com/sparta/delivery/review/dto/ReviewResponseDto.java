package com.sparta.delivery.review.dto;

import java.util.UUID;

public record ReviewResponseDto(
        UUID id,
        UUID restaurantId,
        Long userId,
        double rating,
        String content
) {}
