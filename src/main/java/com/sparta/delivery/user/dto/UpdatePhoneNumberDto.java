package com.sparta.delivery.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePhoneNumberDto(
        @NotBlank(message = "변경할 번호를 입력해주세요.")
        String phoneNumber
) {}
