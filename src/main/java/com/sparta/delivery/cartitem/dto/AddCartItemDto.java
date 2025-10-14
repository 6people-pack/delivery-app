package com.sparta.delivery.cartitem.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddCartItemDto(

        @NotNull(message = "메뉴를 찾을 수 없습니다.")
        UUID menuId,

        String option,

        @NotNull(message = "수량을 입력해주세요.")
        int quantity

) {}
