package com.sparta.delivery.inquiry.mapper;


import com.sparta.delivery.inquiry.domain.Inquiry;
import com.sparta.delivery.inquiry.dto.InquiryCreateRequestDto;
import com.sparta.delivery.inquiry.dto.InquiryItem;
import com.sparta.delivery.inquiry.dto.InquiryOneGetResponseDto;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import shop.matjalalzz.inquiry.dto.InquiryCreateRequest;
import shop.matjalalzz.inquiry.dto.InquiryItem;
import shop.matjalalzz.inquiry.dto.InquiryOneGetResponse;
import shop.matjalalzz.inquiry.entity.Inquiry;
import shop.matjalalzz.user.entity.User;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryMapper {
    public static Inquiry toInquiry(InquiryCreateRequestDto inquiryCreateRequestDto, User user) {
        return Inquiry.builder()
            .title(inquiryCreateRequestDto.title())
            .content(inquiryCreateRequestDto.content())
            .user(user)
            .build();
    }

    public static InquiryOneGetResponseDto fromInquiry(Inquiry inquiry, List<String> images) {
        return new InquiryOneGetResponseDto(inquiry.getTitle(), inquiry.getContent(), images);
    }

    public static InquiryItem fromInquiryItem(Inquiry inquiry, int answerCount) {
        return InquiryItem.builder()
            .InquiryId(inquiry.getId())
            .title(inquiry.getTitle())
            .createTime(inquiry.getCreatedAt())
            .answerCount(answerCount)
            .build();
    }

}
