package com.sparta.delivery.ai.controller;

import com.sparta.delivery.ai.dto.AiAllResponseDto;
import com.sparta.delivery.ai.dto.AiRequestDto;
import com.sparta.delivery.ai.dto.AiResponseDto;
import com.sparta.delivery.ai.dto.AiSimpleResponseDto;
import com.sparta.delivery.ai.service.AiApiService;
import com.sparta.delivery.global.unit.common.BaseResponse;
import com.sparta.delivery.global.unit.common.BaseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class AiApiController {
    private final AiApiService aiApiService;

    @PostMapping
    public BaseResponse<AiSimpleResponseDto> askQuestion (@RequestBody AiRequestDto requestDto) {
        return BaseResponse.ok(aiApiService.getAnswerFromAi(requestDto.getQuestion()), BaseStatus.CREATED);
    }

    @GetMapping
    public BaseResponse<AiAllResponseDto> getAllChats(@RequestParam(required = false) String startday,
                                                      @RequestParam(required = false) String endday,
                                                      @RequestParam(required = false) String word) {
        return BaseResponse.ok(aiApiService.getAllChats(startday, endday,word), BaseStatus.OK);
    }

    @GetMapping("/{ai_id}")
    public BaseResponse<AiResponseDto> getChat(@PathVariable("ai_id") String aiId) {
        return BaseResponse.ok(aiApiService.getChat(aiId), BaseStatus.OK);
    }
}
