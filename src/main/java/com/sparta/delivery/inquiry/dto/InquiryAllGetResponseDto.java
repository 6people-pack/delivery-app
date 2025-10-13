package com.sparta.delivery.inquiry.dto;


import java.util.List;
import lombok.Builder;

@Builder
public record InquiryAllGetResponseDto(
    List<InquiryItem> content,
    Long nextCursor
){}
