package com.sparta.delivery.order.dto;

import com.sparta.delivery.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GetOrderDto(
        LocalDateTime orderedAt,
        OrderStatus orderStatus,
        UUID restaurantId, // 임시, 일단 아이디 저장
        int grossAmount,
        int totalAmount,
        List<GetOrderItemDto> orderItems

) {}
