package com.sparta.delivery.inquiry.controller;

import com.sparta.delivery.global.unit.common.BaseResponse;
import com.sparta.delivery.global.unit.common.BaseStatus;
import com.sparta.delivery.inquiry.dto.InquiryAllGetResponseDto;
import com.sparta.delivery.inquiry.dto.InquiryCreateRequestDto;
import com.sparta.delivery.inquiry.dto.InquiryOneGetResponseDto;
import com.sparta.delivery.inquiry.service.InquiryService;
import com.sparta.delivery.security.userdetails.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class InquiryController {


        private final InquiryService inquiryService;

        @ResponseStatus(HttpStatus.CREATED)
        @PostMapping("/inquiry")
        public BaseResponse<Void> newInquiry(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody @Valid InquiryCreateRequestDto request) {
                inquiryService.createNewInquiry(userDetails.getUser().getId(), request);
                return BaseResponse.ok(BaseStatus.OK);
        }


        @ResponseStatus(HttpStatus.OK)
        @GetMapping("/inquiry")
        public BaseResponse<InquiryAllGetResponseDto> getAllInquiry(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10", required = false) int size) {
            return BaseResponse.ok(inquiryService.getAllInquiry(cursor,size), BaseStatus.OK);
        }



        @ResponseStatus(HttpStatus.OK)
        @GetMapping("/inquiry/{inquiryId}")
        public BaseResponse<InquiryOneGetResponseDto> getOneInquiry(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long inquiryId) {
            return BaseResponse.ok(inquiryService.getOneInquiry(userDetails.getUser().getId(), inquiryId), BaseStatus.OK);
        }

}
