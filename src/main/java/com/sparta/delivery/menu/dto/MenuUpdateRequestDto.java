package com.sparta.delivery.menu.dto;

import com.sparta.delivery.menu.domain.MenuStatus;
import jakarta.validation.constraints.Positive;

public record MenuUpdateRequestDto(
        String name,
        String description,
        @Positive Integer price,
        Integer discountPrice,
        String option,
        MenuStatus status
) {}
