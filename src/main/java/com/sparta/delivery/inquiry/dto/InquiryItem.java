package com.sparta.delivery.inquiry.dto;


import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record InquiryItem (
    UUID InquiryId,
    String title,
    int answerCount,
    LocalDateTime createTime

){}
