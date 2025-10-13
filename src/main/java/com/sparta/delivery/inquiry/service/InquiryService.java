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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    //private final PreSignedProvider preSignedProvider;
    //private final CommentQueryService commentQueryService;
    //private final UserService userService;
    //private final ImageQueryService imageQueryService;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${aws.credentials.AWS_BASE_URL}")
    private String BASE_URL;

    // 문의글 생성
    @Transactional
    public void createNewInquiry(long userId, InquiryCreateRequestDto request) {
        User user = userService.getUserById(userId);
        Inquiry inquiry = InquiryMapper.toInquiry(request, user);
        //inquirySer.createNewInquiry(inquiry);
        eventPublisher.publishEvent(new InquiryCreateEvent(inquiry));
    }


    // 문의글 전체 조회
    public InquiryAllGetResponseDto getAllInquiry(Long cursor, int size) {
        Slice<Inquiry> inquirySlice = inquiryQueryService.getAllInquiry(cursor, size);
        Long nextCursor = null;
        if (inquirySlice.hasNext()) {
            nextCursor = inquirySlice.getContent().getLast().getId();
        }
        List<InquiryItem> inquiryItems = inquirySlice.stream().map(inquiry ->
        {
            int answerCount = commentQueryService.findCommentSize(inquiry.getId());
            return InquiryMapper.fromInquiryItem(inquiry, answerCount);
        }).toList();

        return new InquiryAllGetResponseDto(inquiryItems, nextCursor);
    }


    // 본인이 작성한 하나의 문의글 조회 (관리자도 조회 가능)
    public InquiryOneGetResponseDto getOneInquiry(long userId, Long inquiryId) {
        // 이것도 userQueryService 필요
        User user = userService.getUserById(userId);
        Inquiry inquiry = inquiryQueryService.getOneInquiry(inquiryId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FIND_INQUIRY));

        //관리자도 아니고 자신이 쓴 문의글이 아니면 조회 불가
        if (!user.getRole().equals(Role.ADMIN) && !inquiry.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        List<String> imagesUrl = imageQueryService.findByInquiryImage(inquiry.getId());
        List<String> imagesPathUrl = imagesUrl.stream().map(path -> BASE_URL + path).toList();
        return InquiryMapper.fromInquiry(inquiry, imagesPathUrl);

    }
}