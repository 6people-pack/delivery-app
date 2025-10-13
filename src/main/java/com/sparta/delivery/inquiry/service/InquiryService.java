package com.sparta.delivery.inquiry.service;


import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
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
import com.sparta.delivery.user.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    //private final PreSignedProvider preSignedProvider;
    //private final CommentQueryService commentQueryService;
    //private final ImageQueryService imageQueryService;
    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;


    // 문의글 생성
    @Transactional
    public void createNewInquiry(long userId, InquiryCreateRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Inquiry inquiry = InquiryMapper.toInquiry(request, user);
        inquiryRepository.save(inquiry);
        eventPublisher.publishEvent(new InquiryCreateEvent(inquiry));
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
    public InquiryOneGetResponseDto getOneInquiry(long userId, Long inquiryId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Inquiry inquiry = inquiryRepository.findByIdAndUser(userId, inquiryId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FIND_INQUIRY));

        //관리자도 아니고 자신이 쓴 문의글이 아니면 조회 불가
        if (!user.getRole().equals(Role.ADMIN) && !inquiry.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        return InquiryMapper.fromInquiry(inquiry);

    }
}