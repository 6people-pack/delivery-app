package com.sparta.delivery.inquiry.service;


import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.global.unit.common.BaseResponse;
import com.sparta.delivery.global.unit.common.BaseStatus;
import com.sparta.delivery.inquiry.domain.Inquiry;
import com.sparta.delivery.inquiry.dto.InquiryAllGetResponseDto;
import com.sparta.delivery.inquiry.dto.InquiryCreateRequestDto;
import com.sparta.delivery.inquiry.dto.InquiryItem;
import com.sparta.delivery.inquiry.dto.InquiryOneGetResponseDto;
import com.sparta.delivery.inquiry.event.InquiryCreateEvent;
import com.sparta.delivery.inquiry.mapper.InquiryMapper;
import com.sparta.delivery.inquiry.repository.InquiryRepository;
import com.sparta.delivery.user.domain.Role;
import com.sparta.delivery.user.domain.User;
import com.sparta.delivery.user.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    //private final PreSignedProvider preSignedProvider;
    //private final CommentQueryService commentQueryService;
    //private final ImageQueryService imageQueryService;
    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final ChatClient chatClient;


    // 문의글 생성
    @Transactional
    public void createNewInquiry(long userId, InquiryCreateRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Inquiry inquiry = InquiryMapper.toInquiry(request, user);
        inquiryRepository.save(inquiry);
        eventPublisher.publishEvent(new InquiryCreateEvent(inquiry, user));
    }


    // 문의글 전체 조회
    @Transactional(readOnly = true)
    public InquiryAllGetResponseDto getAllInquiry(Long cursor, int size) {

        Slice<Inquiry> inquirySlice = inquiryRepository.findByCursor(cursor, PageRequest.of(0, size));
        UUID nextCursor = null;

        List<Inquiry> content = inquirySlice.getContent(); // 자바 17에서 getLast 메서드가 안되므로 사용
        if (inquirySlice.hasNext()) {
            nextCursor = content.get(content.size()-1).getId();
        }
        List<InquiryItem> inquiryItems = inquirySlice.stream().map(inquiry -> {

            //int answerCount = commentQueryService.findCommentSize(inquiry.getId());
            int answerCount = 0; //TODO 댓글 작업 필요

            return InquiryMapper.fromInquiryItem(inquiry, answerCount);
        }).toList();

        return new InquiryAllGetResponseDto(inquiryItems, nextCursor);
    }


    // 본인이 작성한 하나의 문의글 조회 (관리자도 조회 가능)
    @Transactional(readOnly = true)
    public InquiryOneGetResponseDto getOneInquiry(long userId, UUID inquiryId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Inquiry inquiry = inquiryRepository.findByIdAndUser(inquiryId, userId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FIND_INQUIRY));

        //관리자도 아니고 자신이 쓴 문의글이 아니면 조회 불가
        if (!user.getRole().equals(Role.ADMIN) && !inquiry.getUserId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        return InquiryMapper.fromInquiry(inquiry);

    }

    // 해당 위치에서 변경 예정

    public BaseResponse<String> getAiAnswer(String message){
        try {
            String answer = chatClient
                .prompt()
                .user(message)
                .call()
                .content();

            if (answer == null || answer.isBlank()) {
                answer = "응답이 완성되질 않음";
            }
            return BaseResponse.ok(answer, BaseStatus.OK);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.AI_SERVICE_UNAVAILABLE);
        }
    }
}
