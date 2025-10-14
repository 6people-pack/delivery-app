package com.sparta.delivery.user.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyPasswordDto(
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String currentPassword
) {}
