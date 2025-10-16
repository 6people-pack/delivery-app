package com.sparta.delivery.ai.controller;

import com.sparta.delivery.ai.dto.*;
import com.sparta.delivery.ai.service.AiApiService;
import com.sparta.delivery.ai.service.AiPromptService;
import com.sparta.delivery.ai.service.AiService;
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
    private final AiService aiService;
    private final AiPromptService aiPromptService;

    //ai요청 내용을 키워드로 받는 api, 원래는 front 영역
    @PostMapping("/keywords")
    public BaseResponse<AiSimpleResponseDto> askKeywordQuestion (@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                   @RequestBody @Valid AiKeywordsRequestDto requestDto) {
        return BaseResponse.ok(aiPromptService.GetAiWithKeywords(userDetails.getUser().getId(),requestDto), BaseStatus.CREATED);
    }

    //ai에게 질문 요청api, 원래 front가 있으면 이 api가 있는게 맞음
    @PostMapping
    public BaseResponse<AiSimpleResponseDto> askQuestion (@RequestBody @Valid AiRequestDto requestDto) {
        return BaseResponse.ok(aiApiService.getAnswerFromAi(requestDto.getQuestion()), BaseStatus.CREATED);
    }

    //ai가 답변한 내용 전체 조회
    //startday, endday, word(키워드)로 검색 가능
    @GetMapping
    public BaseResponse<AiAllResponseDto> getAllChats(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @RequestParam(required = false) String startday,
                                                      @RequestParam(required = false) String endday,
                                                      @RequestParam(required = false) String word) {
        return BaseResponse.ok(aiService.getAllChats(userDetails.getUser().getId(),startday, endday,word), BaseStatus.OK);
    }

    //ai가 답변한 내용 단건 조회
    @GetMapping("/{aiId}")
    public BaseResponse<AiResponseDto> getChat(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @PathVariable("aiId") String aiId) {
        return BaseResponse.ok(aiService.getChat(userDetails.getUser().getId(),aiId), BaseStatus.OK);
    }

}
