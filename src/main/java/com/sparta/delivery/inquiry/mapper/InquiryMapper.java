package com.sparta.delivery.inquiry.mapper;


import com.sparta.delivery.inquiry.domain.Inquiry;
import com.sparta.delivery.inquiry.dto.InquiryCreateRequestDto;
import com.sparta.delivery.inquiry.dto.InquiryItem;
import com.sparta.delivery.inquiry.dto.InquiryOneGetResponseDto;
import com.sparta.delivery.user.domain.User;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryMapper {
    public static InquiryOneGetResponseDto fromInquiry(Inquiry inquiry) {
        return new InquiryOneGetResponseDto(inquiry.getTitle(), inquiry.getContent());
    }

    public static InquiryItem fromInquiryItem(Inquiry inquiry, long answerCount) {
        return InquiryItem.builder()
            .InquiryId(inquiry.getId())
            .title(inquiry.getTitle())
            .createTime(inquiry.getCreatedAt())
            .answerCount(answerCount)
            .build();
    }

}
