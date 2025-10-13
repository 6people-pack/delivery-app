package com.sparta.delivery.user.dto;


import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(

        //TODO  NotBlank 작업 필요 -> 완

        @NotBlank(message = "이메일 입력은 필수 입력값입니다.")
        String email,

        @NotBlank(message = "비밀번호 입력은 필수 입력값입니다.")
        String password
) {}