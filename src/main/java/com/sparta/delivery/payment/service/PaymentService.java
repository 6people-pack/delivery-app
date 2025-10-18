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
import com.sparta.delivery.user.domain.User;
import com.sparta.delivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

	private final TossApiClient tossApiClient;
	private final PaymentRepository paymentRepository;
	private final UserRepository userRepository;

	@Value("${toss.secret-key}")
	private String secretKey;

    // 프론트에서 orderId, paymentKey 받아서 들어오는 결재 요청 서비스
	@Transactional(noRollbackFor = org.springframework.web.server.ResponseStatusException.class)
	public PaymentSuccessResponse confirmPayment(User user, TossPaymentConfirmRequest tossRequest) {

		Long point = tossRequest.amount();

		if (point < 1) {
			throw new BusinessException(ErrorCode.ZERO_AMOUNT_PAYMENT_NOT_ALLOWED); // 0원 결제 시 400
		}


        // 여기에서 굳이 한번 더 확인해야 되는지 궁금
		User findUser = userRepository.findById(user.getId()).orElseThrow(() ->
                new BusinessException(ErrorCode.USER_NOT_FOUND));

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

        // 결재 정보 저장은 유저아이디, 이름, 주문아이디, 결재금액, 페이먼트키
		paymentRepository.save(PaymentMapper.toPayment(findUser.getId(), findUser.getName(), UUID.fromString(tossRequest.orderId()), tossRequest.amount(), "ok", tossRequest.paymentKey()));

        // 응답은 주문 아이디와 결재 금액만
		return PaymentSuccessResponse.builder()
            .orderId(tossRequest.orderId())
            .amount(tossRequest.amount()).build();
	}
}