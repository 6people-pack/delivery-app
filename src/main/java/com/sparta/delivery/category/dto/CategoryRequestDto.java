package com.sparta.delivery.category.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDto (
        @NotBlank(message = "카테고리 이름은 필수입력값 입니다.")
        String name
) {}
