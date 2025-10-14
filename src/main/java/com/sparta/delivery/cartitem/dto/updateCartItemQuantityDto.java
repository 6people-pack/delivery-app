package com.sparta.delivery.cartitem.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record updateCartItemQuantityDto(

        @NotNull(message = "메뉴가 존재하지 않습니다.")
        UUID cartItemId,

        boolean increase

) {}
