package com.sparta.delivery.inquiry.service;

import com.sparta.delivery.comment.domain.Comment;
import com.sparta.delivery.comment.repository.CommentRepository;
import com.sparta.delivery.comment.service.CommentService;
import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.global.unit.common.BaseResponse;
import com.sparta.delivery.global.unit.common.BaseStatus;
import com.sparta.delivery.inquiry.domain.Inquiry;
import com.sparta.delivery.inquiry.exception.RetryableAiException;
import com.sparta.delivery.inquiry.repository.InquiryRepository;
import com.sparta.delivery.user.domain.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiResponder {

    private final CommentService commentService;
    private final ChatClient chatClient;


    public void aiAnswerComment(User user, Inquiry inquiry) {
        String answer = chatClient
            .prompt()
            .user(inquiry.getContent())
            .call()
            .content();

        if (answer == null || answer.isBlank()) {
            throw new RetryableAiException("Blank content from LLM");
        }

        commentService.saveAiComment(answer, user, inquiry);

    }


    // 폴백에서 호출
    public void sendToDlq(UUID inquiryId, Throwable cause) {
        // TODO: DLQ 테이블 작성  (모르겠음)

        log.error("[AI-DLQ] inquiryId={}, cause={}", inquiryId, cause == null ? "unknown" : cause.toString());
    }


}
