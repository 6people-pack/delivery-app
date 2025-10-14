package com.sparta.delivery.order.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderDto(
        @NotBlank(message = "주소를 입력하세요.")
        String address,

        @NotBlank(message = "상세 주소를 입력하세요.")
        String addressDetail,

        String customerRequest

) {}
