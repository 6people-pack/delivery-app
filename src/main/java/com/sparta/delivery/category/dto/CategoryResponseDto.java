package com.sparta.delivery.category.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CategoryResponseDto(
        UUID categoryId,
        String name
) {
}

