package com.sparta.delivery.payment.mapper;

import com.sparta.delivery.payment.domain.Payment;

public class PaymentMapper {

    public static Payment toPayment(long amount, String paymentKey, String statusMessage ) {
        return Payment.builder()
            .amount(amount)
            .paymentKey(paymentKey)
            .payStatusMessage(statusMessage).build();
    }

}
