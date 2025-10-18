package com.sparta.delivery.payment.mapper;

import com.sparta.delivery.payment.domain.Payment;

import java.util.UUID;

public class PaymentMapper {

    public static Payment toPayment(Long userId, String userName, UUID orderId, long amount, String statusMessage, String paymentKey) {
        return Payment.builder()
                .userId(userId)
                .userName(userName)
                .orderId(orderId)
                .amount(amount)
                .payStatusMessage(statusMessage)
                .paymentKey(paymentKey)
                .build();
    }

}
