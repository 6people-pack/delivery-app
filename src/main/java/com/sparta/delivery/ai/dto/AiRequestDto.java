package com.sparta.delivery.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AiRequestDto {
    @NotBlank(message = "카테고리는 필수 입력입니다.")
    private final String category;

    @NotNull(message = "카테고리 아이디는 필수 입력입니다.")
    private final UUID categoryId;
    //질문은 1자 이상 300자 이하로 제한
    @Length(min = 1, max = 300, message="질문은 1자 이상 300자 이하로 작성해주세요.")
    private final String question;
}
