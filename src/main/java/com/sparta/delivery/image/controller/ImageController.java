package com.sparta.delivery.image.controller;

import com.sparta.delivery.global.unit.common.BaseResponse;
import com.sparta.delivery.global.unit.common.BaseStatus;
import com.sparta.delivery.image.dto.*;
import com.sparta.delivery.image.service.ImageService;
import com.sparta.delivery.security.userdetails.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    //이미지 다건 조회
    //해당 카테고리의 이미지가 없을시 기본 이미지 반환
    @GetMapping("/all/{category}")
    public BaseResponse<ImageMultiResponseDto> getAllImage(@PathVariable String category,
                                                           @RequestBody ImageMultiRequestDto requestDto) {
        return BaseResponse.ok(imageService.getAllImage(category, requestDto.getCategoryid()), BaseStatus.OK);
    }
    //이미지 다건 수정
    @PutMapping("/all")
    public BaseResponse<ImageMultiResponseDto> updateAllImage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @RequestPart("files") List<MultipartFile> files,
                                                              @RequestPart("request") String request) {
        return BaseResponse.ok(imageService.updateAllImage(userDetails.getUser().getId(), request, files), BaseStatus.OK);
    }
}
