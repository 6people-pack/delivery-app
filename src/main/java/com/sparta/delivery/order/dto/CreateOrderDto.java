package com.sparta.delivery.order.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateOrderDto(
        @NotBlank(message = "주소를 입력하세요.")
        String address,

        @NotBlank(message = "상세 주소를 입력하세요.")
        String addressDetail,

        UUID restaurantId,

        String customerRequest

) {}
