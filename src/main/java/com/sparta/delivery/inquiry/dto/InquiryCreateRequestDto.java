package com.sparta.delivery.inquiry.dto;


import jakarta.validation.constraints.NotBlank;

public record InquiryCreateRequestDto(

    @NotBlank(message = "문의 사항 제목을 입력해 주세요.")
    String title,

    @NotBlank(message = "문의 내용을 입력해 주세요")
    String content

)
{}