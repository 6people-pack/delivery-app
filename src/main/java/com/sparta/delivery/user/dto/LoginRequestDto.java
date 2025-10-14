package com.sparta.delivery.user.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequestDto(

        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        @NotBlank(message = "이메일 입력은 필수 입력값입니다.")
        String email,

        @NotBlank(message = "비밀번호 입력은 필수 입력값입니다.")
        String password
) {}