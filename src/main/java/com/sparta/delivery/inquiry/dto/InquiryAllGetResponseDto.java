package com.sparta.delivery.inquiry.dto;


import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record InquiryAllGetResponseDto(
    List<InquiryItem> content,
    UUID nextCursor
){}
