package com.sparta.delivery.menu.dto;

import com.sparta.delivery.menu.domain.MenuStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record MenuResponse(
        UUID id,
        UUID restaurantId,
        String name,
        String description,
        int price,
        int discountPrice,
        String option,
        MenuStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}