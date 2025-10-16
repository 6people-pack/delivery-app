package com.sparta.delivery.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordRequestDto(
        @NotBlank(message = "변경할 비밀번호를 입력해주세요.")
        String newPassword,     // 실제 변경할 비밀번호

        @NotBlank(message = "변경할 비밀번호를 한번 더 입력해주세요.")
        String confirmPassword  // 새 비밀번호 확인용
) {}
