package com.sparta.delivery.ai.controller;

import com.sparta.delivery.ai.dto.AiRequestDto;
import com.sparta.delivery.ai.dto.AiResponseDto;
import com.sparta.delivery.ai.service.AiApiService;
import com.sparta.delivery.global.unit.common.BaseResponse;
import com.sparta.delivery.global.unit.common.BaseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiApiController {
    private final AiApiService aiApiService;

    @PostMapping("/question")
    public BaseResponse<AiResponseDto> askQuestion (@RequestBody AiRequestDto requestDto) {
        return BaseResponse.ok(aiApiService.getAnswerFromAi(requestDto.getQuestion()), BaseStatus.OK);
    }
}
