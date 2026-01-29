package com.sparta.delivery.global.dlq.service;

import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.inquiry.domain.Inquiry;
import com.sparta.delivery.inquiry.repository.InquiryRepository;
import com.sparta.delivery.inquiry.service.AiResponder;
import com.sparta.delivery.user.domain.User;
import com.sparta.delivery.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecoveryService {

    private final AiResponder aiResponder;
    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    @Transactional
    public void reprocessOnce(UUID inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new IllegalStateException("Inquiry not found: " + inquiryId));
        User user = userRepository.findById(inquiry.getUserId()).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 기존 AI 답변 로직 재실행
        aiResponder.aiAnswerComment(user, inquiry);
    }
}
