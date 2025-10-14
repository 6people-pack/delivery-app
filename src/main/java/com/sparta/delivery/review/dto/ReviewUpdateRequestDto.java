package com.sparta.delivery.review.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public record ReviewUpdateRequestDto(
        @DecimalMin("1.0") @DecimalMax("5.0") Double rating,
        @Size(max = 2000) String content
) {}