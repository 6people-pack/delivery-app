package com.sparta.delivery.inquiry.listener;

import com.sparta.delivery.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InquiryAiEventListener {

    private final AiResponder aiResponder;

}
