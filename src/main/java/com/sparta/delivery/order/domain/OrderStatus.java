package com.sparta.delivery.order.domain;

public enum OrderStatus {
    PENDING, ACCEPTED, COOKING, DISPATCHED,
    COMPLETED,  // 결제/주문 완료
    DELIVERED,  // 배송 완료
    CANCELLED
}