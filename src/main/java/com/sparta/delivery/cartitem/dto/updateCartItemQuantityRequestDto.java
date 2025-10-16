package com.sparta.delivery.cartitem.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record updateCartItemQuantityRequestDto(

        @NotNull(message = "메뉴를 입력해주세요.")
        UUID cartItemId,

        @NotNull(message = "수량을 추가할지 말지 입력하세요.")
        Boolean increase

) {}
