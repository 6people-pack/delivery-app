package com.sparta.delivery.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateOrderResponseDto(

        @NotBlank(message = "주문ID를 입력해주세요.")
        UUID orderId,

        @NotNull(message = "주문의 결재 금액을 입력해주세요.")
        int totalAmount
) {
}
