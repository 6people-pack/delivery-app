package com.sparta.delivery.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateNicknameDto(
        @NotBlank(message = "변경할 닉네임을 입력해주세요.")
        String nickname
) {}
