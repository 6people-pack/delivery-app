package com.sparta.delivery.review.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReviewCreateRequestDto(
        @NotNull UUID restaurantId,
        @NotNull UUID orderId,
        @DecimalMin("1.0") @DecimalMax("5.0") double rating,
        @NotBlank String content
) {}