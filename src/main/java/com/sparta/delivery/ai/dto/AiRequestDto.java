package com.sparta.delivery.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

/**
 * @param question 질문은 1자 이상 300자 이하로 제한
 */
public record AiRequestDto(@NotBlank(message = "카테고리는 필수 입력입니다.") String category,
                           @NotNull(message = "카테고리 아이디는 필수 입력입니다.") UUID categoryId,
                           @Length(min = 1, max = 300, message = "질문은 1자 이상 300자 이하로 작성해주세요.") String question) {
}
