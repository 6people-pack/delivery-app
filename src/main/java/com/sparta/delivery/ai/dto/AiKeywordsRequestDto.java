package com.sparta.delivery.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class AiKeywordsRequestDto {
    @NotBlank(message = "카테고리는 필수 입력입니다.")
    private String category;
    @NotBlank(message = "카테고리 아이디는 필수 입력입니다.")
    @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "유효한 UUID가 아닙니다.")
    private String categoryId;
    @NotBlank(message = "키워드를 입력해주세요.")
    private String keywords;
}
