package com.sparta.delivery.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GetOrderItemDetailResponseDto(
        @NotBlank(message = "메뉴 이름을 입력하세요.")
        String menuName,

        @NotNull(message = "메뉴의 가격을 입력해주세요.")
        int menuPrice,

        int menuDiscountPrice,

        @NotNull(message = "수량을 입력하세요.")
        int quantity,

        String option
) {
}