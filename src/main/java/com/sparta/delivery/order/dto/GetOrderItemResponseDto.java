package com.sparta.delivery.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GetOrderItemResponseDto(

        @NotBlank(message = "메뉴 이름을 입력하세요.")
        String menuName,

        @NotNull(message = "수량을 입력하세요.")
        int quantity,

        String option

) {}
