package com.sparta.delivery.inquiry.dto;


import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record InquiryItem (
    long InquiryId,
    String title,
    int answerCount,
    LocalDateTime createTime

){}
