package com.sparta.delivery.cartitem.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GetCartItemResponseDto(

        @NotNull(message = "메뉴 데이터가 존재하지 않습니다.")
        UUID menuId,

        String option,

        @NotNull(message = "수량 데이터가 존재하지 않습니다.")
        int quantity
) {}
