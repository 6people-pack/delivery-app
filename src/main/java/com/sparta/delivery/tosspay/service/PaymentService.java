package com.sparta.delivery.tosspay.service;

import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.tosspay.config.TossApiClient;
import com.sparta.delivery.tosspay.dto.PaymentSuccessResponse;
import com.sparta.delivery.tosspay.dto.TossPaymentConfirmRequest;
import com.sparta.delivery.tosspay.dto.TossPaymentConfirmResponse;
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
//	private final UserRepository userRepository;

	@Value("${toss.secret-key}")
	private String secretKey;

	@Transactional
	public PaymentSuccessResponse confirmPayment(TossPaymentConfirmRequest tossRequest) {

		//유저 검증
//		Long userId = userContextService.getCurrentUserId();
//		Optional<User> userOptional = userRepository.findById(userId);
//		if (userOptional.isEmpty()) {
//			throw new BusinessException(ErrorCode.USER_NOT_FOUND);
//		}

		// 2) Toss 승인 호출 (상태/본문 로깅)
		final TossPaymentConfirmResponse tossResponse;
		try {
			tossResponse = tossApiClient.confirmPayment(tossRequest);
		} catch (feign.FeignException e) {
			int status = e.status();
			String body = e.contentUTF8();

			//TODO 배포 시 빼야 함
			log.error("Toss confirm failed: status={}, body={}", status, body);

			// Toss 쪽 오류를 그대로 가시화(디버깅/정합성 체크에 유리)
			//  - 401/403: 키/인증 문제
			//  - 400/409: amount, orderId 불일치 등 정합성 문제
			throw new org.springframework.web.server.ResponseStatusException(
				org.springframework.http.HttpStatus.valueOf(Math.max(400, status)), // 최소 400로
				"Toss confirm error: " + status + " / " + body, e
			);
		}

		if (!"DONE".equals(tossResponse.status())) {
			throw new BusinessException(ErrorCode.INVALID_REQUEST_DATA); //400
		}

		//User user = userOptional.get();
		Long point = tossRequest.amount();

		if (point < 1) {
			throw new BusinessException(ErrorCode.ZERO_AMOUNT_PAYMENT_NOT_ALLOWED); // 0원 결제 시 400
		}
		//user.updatePoint(user.getPoint() + point);

		//객체 생성 후 반환 코드 필요

        return PaymentSuccessResponse.builder()
            .orderId(tossRequest.orderId())
            .amount(tossRequest.amount()).build();
	}
}