package com.sparta.delivery.order.dto;

import com.sparta.delivery.order.domain.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record GetOrderDetailResponseDto(

        @NotBlank(message = "주문 번호를 입력하세요.")
        String orderNumber,

        @NotNull(message = "orderStatus를 입력하세요.")
        OrderStatus orderStatus,

        @NotBlank(message = "address를 입력하세요.")
        String address,

        @NotBlank(message = "addressDetail를 입력하세요.")
        String addressDetail,

        @NotBlank(message = "grossAmount를 입력하세요.")
        int grossAmount,

        @NotBlank(message = "deliveryFee를 입력하세요.")
        int deliveryFee,

        @NotBlank(message = "discountAmount를 입력하세요.")
        int discountAmount,

        @NotBlank(message = "totalAmount를 입력하세요.")
        int totalAmount,

        String customerRequest,

        @NotBlank(message = "주문 시각을 입력하세요.")
        LocalDateTime orderedAt,

        LocalDateTime acceptedAt,

        LocalDateTime deliveringAt,

        LocalDateTime deliveredAt,

        LocalDateTime canceledAt,

        String cancelMessage,

        @NotBlank(message = "주문 아이템을 입력하세요.")
        List<GetOrderItemDetailResponseDto> orderItems
) {

}
