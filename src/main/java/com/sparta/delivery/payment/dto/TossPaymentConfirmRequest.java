package com.sparta.delivery.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TossPaymentConfirmRequest(
	@NotBlank(message = "결제 키(paymentKey)는 필수 입력값입니다.") String paymentKey,

	@NotBlank(message = "주문 번호(orderId)는 필수 입력값입니다.") String orderId,

	@NotNull(message = "결제 가격(amount)은 필수 입력값입니다.") Long amount) {
}
