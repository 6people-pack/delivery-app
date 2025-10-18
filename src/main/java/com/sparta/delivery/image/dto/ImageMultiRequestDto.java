package com.sparta.delivery.image.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ImageMultiRequestDto(
        @NotNull UUID categoryId) {
}
