package com.sparta.delivery.payment.service;


import com.sparta.delivery.payment.mapper.PaymentMapper;
import com.sparta.delivery.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentAuditService {

    private final PaymentRepository paymentRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(Long amount, String orderId, String msg) {
        String safeMessage = safeTrim(msg, 1000);
        paymentRepository.saveAndFlush(PaymentMapper.toPayment(amount, orderId, safeMessage));
    }

    private static String safeTrim(String s, int max) {
        if (s == null) return "null";
        s = s.strip();                    // 유니코드 공백 제거
        return s.length() > max ? s.substring(0, max) : s;
    }

}
