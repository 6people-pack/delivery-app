package com.sparta.delivery.user.dto;

import com.sparta.delivery.user.domain.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

// Up 부분 대문자 아닌데 파일명 고치기가 안됨..

public record SignUpRequestDto(

    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    @NotBlank(message = "이메일 입력은 필수 입력값입니다.")
    String email,

    @NotBlank(message = "비밀번호 입력은 필수 입력값입니다.")
    String password,

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    String nickname,

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    String phoneNumber
) {}
