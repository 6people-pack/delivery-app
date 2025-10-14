package com.sparta.delivery.payment.dto;

public record TossPaymentFailLogRequest (String orderId, Long amount, String code, String message){}
