package com.sparta.delivery.security.jwt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record RefreshTokenResponseDto(

        @NotBlank(message = "jwt 토큰을 입력해주세요.")
        String token,

        @NotNull(message = "토큰의 만료 시간을 입력해주세요.")
        Date exp
) {}

