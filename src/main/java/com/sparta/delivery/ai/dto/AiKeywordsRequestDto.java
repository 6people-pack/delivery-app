package com.sparta.delivery.ai.dto;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonPropertyOrder({ "category", "categoryId", "keywords" })
@NoArgsConstructor
@AllArgsConstructor
public class AiKeywordsRequestDto {
    @NotBlank(message = "카테고리는 필수 입력입니다.")
    private String category;

    @NotBlank(message = "카테고리 아이디는 필수 입력입니다.")
    @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "유효한 UUID가 아닙니다.")
    private String categoryId;

    @NotNull(message = "키워드를 입력해주세요.") @Valid  //@NotBlank는 String만 가능
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY, //  바깥 DTO의 "category" 값으로 타입 결정
            property = "category"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = RestaurantKeywords.class, name = "restaurant"),
            @JsonSubTypes.Type(value = MenuKeywords.class, name = "menu"),
    })
    private Keywords keywords;
}

