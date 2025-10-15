package com.sparta.delivery.ai.dto;

import jakarta.validation.constraints.NotBlank;

public record MenuKeywords(
        @NotBlank String name,
        @NotBlank String category,
        @NotBlank String mainIngredient,           // 주재료
        @NotBlank String highlight        // 중점으로 설명하고 싶은 점
) implements Keywords { }
