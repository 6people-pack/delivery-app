package com.sparta.delivery.payment.service;

import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.payment.config.TossApiClient;
import com.sparta.delivery.payment.domain.Payment;
import com.sparta.delivery.payment.dto.PaymentSuccessResponse;
import com.sparta.delivery.payment.dto.TossPaymentConfirmRequest;
import com.sparta.delivery.payment.dto.TossPaymentConfirmResponse;
import com.sparta.delivery.payment.mapper.PaymentMapper;
import com.sparta.delivery.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

	private final TossApiClient tossApiClient;
	private final PaymentRepository paymentRepository;
//	private final UserRepository userRepository;

	@Value("${toss.secret-key}")
	private String secretKey;

	@Transactional(noRollbackFor = org.springframework.web.server.ResponseStatusException.class)
	public PaymentSuccessResponse confirmPayment(TossPaymentConfirmRequest tossRequest) {

		Long point = tossRequest.amount();

		if (point < 1) {
			throw new BusinessException(ErrorCode.ZERO_AMOUNT_PAYMENT_NOT_ALLOWED); // 0원 결제 시 400
		}

//		TODO 프론트 LOGIN 후 유저 정보 설정 필요 (프론트 코드 이슈)
//		Long userId = userContextService.getCurrentUserId();
//		Optional<User> userOptional = userRepository.findById(userId);
//		if (userOptional.isEmpty()) {
//			throw new BusinessException(ErrorCode.USER_NOT_FOUND);
//		}

		final TossPaymentConfirmResponse tossResponse;
		try {
			tossResponse = tossApiClient.confirmPayment(tossRequest);
		} catch (feign.FeignException e) {
			int status = e.status();
			String statusMessage = e.contentUTF8();


			// Toss 쪽 오류를 그대로 가시화(디버깅/정합성 체크에 유리)
			//  - 401/403: 키/인증 문제
			//  - 400/409: amount, orderId 불일치 등 정합성 문제
			throw new org.springframework.web.server.ResponseStatusException(
				org.springframework.http.HttpStatus.valueOf(Math.max(400, status)),
				"Toss confirm error: " + status + " / " + statusMessage, e
			);
		}

		if (!"DONE".equals(tossResponse.status())) {
			throw new BusinessException(ErrorCode.INVALID_REQUEST_DATA); //400
		}


		paymentRepository.save(PaymentMapper.toPayment(tossRequest.amount(), tossRequest.orderId(), "ok"));

		return PaymentSuccessResponse.builder()
            .orderId(tossRequest.orderId())
            .amount(tossRequest.amount()).build();
	}
}