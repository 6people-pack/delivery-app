package com.sparta.delivery.payment.dto;

/**
 * @param status  결제 상태 "DONE" */

public record TossPaymentConfirmResponse(
        // 결재 요청을 보내면 api에서 보내주는 응답. 모름, 건들 수 없음
        String orderId,
        String paymentKey,
        String status,
        String requestedAt,
        String approvedAt,
        int totalAmount
){ }
