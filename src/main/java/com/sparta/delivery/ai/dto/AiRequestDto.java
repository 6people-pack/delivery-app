package com.sparta.delivery.ai.dto;

import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class AiRequestDto {
    //질문은 1자 이상 300자 이하로 제한
    @Length(min = 1, max = 300, message="질문은 1자 이상 300자 이하로 작성해주세요.")
    private String question;
}
