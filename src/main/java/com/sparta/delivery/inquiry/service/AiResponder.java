package com.sparta.delivery.inquiry.service;

import com.sparta.delivery.comment.service.CommentService;
import com.sparta.delivery.global.dlq.domain.DlqMessage;
import com.sparta.delivery.global.dlq.domain.DlqStatus;
import com.sparta.delivery.global.dlq.repository.DlqMessageRepository;

import com.sparta.delivery.inquiry.domain.Inquiry;
import com.sparta.delivery.inquiry.exception.RetryableAiException;

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
    private final DlqMessageRepository dlqMessageRepository;


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

    // 3회 실패 시 호출되어 실행하는 메서드
    @Transactional
    public void sendToDlq(UUID inquiryId, Throwable cause) {
        String causeMsg = cause == null ? "Unknown error" : cause.getClass().getName() + ": " + cause.getMessage();
        DlqMessage dlq = DlqMessage.createDlq(DlqStatus.PENDING, causeMsg, inquiryId);
        dlqMessageRepository.save(dlq);
    }
}
