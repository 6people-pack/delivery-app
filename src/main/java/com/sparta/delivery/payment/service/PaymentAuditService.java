package com.sparta.delivery.payment.service;


import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.payment.mapper.PaymentMapper;
import com.sparta.delivery.payment.repository.PaymentRepository;
import com.sparta.delivery.user.domain.User;
import com.sparta.delivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentAuditService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(User user, Long amount, String orderId, String msg) {
        String safeMessage = safeTrim(msg, 1000);

        User findUser = userRepository.findById(user.getId()).orElseThrow(() ->
                new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 결재 실패 시에는 페이먼트키 안 받아옴. 실수거나 성공할 때만 받는 듯함
        paymentRepository.saveAndFlush(PaymentMapper.toPayment(findUser.getId(), findUser.getName(), UUID.fromString(orderId), amount, safeMessage, null));
    }

    private static String safeTrim(String s, int max) {
        if (s == null) return "null";
        s = s.strip();                    // 유니코드 공백 제거
        return s.length() > max ? s.substring(0, max) : s;
    }

}
