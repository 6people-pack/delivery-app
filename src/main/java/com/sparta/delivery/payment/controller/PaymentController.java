package com.sparta.delivery.payment.controller;

import com.sparta.delivery.global.unit.common.BaseResponse;
import com.sparta.delivery.global.unit.common.BaseStatus;
import com.sparta.delivery.payment.dto.PaymentSuccessResponse;
import com.sparta.delivery.payment.dto.TossPaymentConfirmRequest;
import com.sparta.delivery.payment.dto.TossPaymentFailLogRequest;
import com.sparta.delivery.payment.service.PaymentAuditService;
import com.sparta.delivery.payment.service.PaymentService;
import com.sparta.delivery.security.userdetails.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Validated
@Slf4j
public class PaymentController {

	private final PaymentService paymentService;
	private final PaymentAuditService paymentAuditService;

    // 프론트에서 orderId, paymentKey 받아서 들어오는 결재 요청 컨트롤러
	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/confirm")
	public BaseResponse<PaymentSuccessResponse> confirm(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid TossPaymentConfirmRequest request) {

		PaymentSuccessResponse response = paymentService.confirmPayment(userDetails.getUser(), request);
		return BaseResponse.ok(response, BaseStatus.CREATED);
	}

	@PostMapping("/fail")
	@ResponseStatus(HttpStatus.OK)
	public void fail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody TossPaymentFailLogRequest req) {
		log.info("[FAIL API] orderId={}, amount={}, message={}", req.orderId(), req.amount(), req.message());
		paymentAuditService.recordFailure(userDetails.getUser(), req.amount(), req.orderId(), req.message());
	}

	@PostMapping("/ping")
	@ResponseStatus(HttpStatus.OK)
	public void ping() { log.info("[PING] /api/payments/ping called"); }
}