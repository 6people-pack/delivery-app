package com.sparta.delivery.user.dto;

import java.util.Date;

public record RefreshTokenDto(
        String token,
        Date exp
) {}

