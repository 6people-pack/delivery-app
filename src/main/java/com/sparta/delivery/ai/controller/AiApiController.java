package com.sparta.delivery.ai.controller;

import com.sparta.delivery.ai.dto.*;
import com.sparta.delivery.ai.service.AiApiService;
import com.sparta.delivery.global.unit.common.BaseResponse;
import com.sparta.delivery.global.unit.common.BaseStatus;
import com.sparta.delivery.security.userdetails.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class AiApiController {
    private final AiApiService aiApiService;

    @PostMapping("/keywords")
    public BaseResponse<AiSimpleResponseDto> askKeywordQuestion (@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                   @RequestBody @Valid AiKeywordsRequestDto requestDto) {
        return BaseResponse.ok(aiApiService.GetAiWithKeywords(userDetails.getUser().getId(),requestDto), BaseStatus.CREATED);
    }

//    @PostMapping
//    public BaseResponse<AiSimpleResponseDto> askQuestion (@RequestBody @Valid AiRequestDto requestDto) {
//        return BaseResponse.ok(aiApiService.getAnswerFromAi(requestDto.getQuestion()), BaseStatus.CREATED);
//    }

    @GetMapping
    public BaseResponse<AiAllResponseDto> getAllChats(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @RequestParam(required = false) String startday,
                                                      @RequestParam(required = false) String endday,
                                                      @RequestParam(required = false) String word) {
        return BaseResponse.ok(aiApiService.getAllChats(userDetails.getUser().getId(),startday, endday,word), BaseStatus.OK);
    }

    @GetMapping("/{aiId}")
    public BaseResponse<AiResponseDto> getChat(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @PathVariable("aiId") String aiId) {
        return BaseResponse.ok(aiApiService.getChat(userDetails.getUser().getId(),aiId), BaseStatus.OK);
    }

}
