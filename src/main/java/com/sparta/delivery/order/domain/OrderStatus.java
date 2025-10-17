package com.sparta.delivery.order.domain;

public enum OrderStatus {
    PENDING, // 결재 대기 상태
    REQUESTED,
    ACCEPTED,
    DELIVERING,
    DELIVERED,
    CANCELED
}