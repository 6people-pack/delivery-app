package com.sparta.delivery.menu.dto;

import com.sparta.delivery.menu.domain.MenuStatus;
import jakarta.validation.constraints.*;
import java.util.UUID;

public record MenuCreateRequest(
        @NotNull UUID restaurantId,
        @NotBlank @Size(max = 30) String name,
        @Positive int price,
        String description,
        Integer discountPrice,   // null이면 0으로 처리
        String option,
        MenuStatus status        // null이면 엔티티에서 SALE 기본값
) {}