package com.sparta.delivery.order.dto;

public record GetOrderItemDto(
        String menuName,
        int quantity,
        String option

) {}
