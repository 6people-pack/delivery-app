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

    //이미지 추가(다수 가능)
//    @PostMapping("/{category}" )
//    public BaseResponse<ImageMultiResponseDto> uploadImage(@AuthenticationPrincipal UserDetailsImpl userDetails,
//                                                           @RequestPart("files") List<MultipartFile> files,
//                                                           @PathVariable String category,
//                                                           @RequestPart("categoryid") String categoryid) {
//        return BaseResponse.ok(imageService.uploadImage(userDetails.getUser().getId(), category,categoryid, files), BaseStatus.CREATED);
//    }

    //이미지 조회
//    @GetMapping("/{category}")
//    public BaseResponse<String> getImage(@AuthenticationPrincipal UserDetailsImpl userDetails,
//                                         @PathVariable String category,
//                                         @RequestBody ImageRequestDto requestDto) {
//        return BaseResponse.ok(imageService.getImage(userDetails.getUser().getId(),category, requestDto), BaseStatus.OK);
//    }

    //이미지 다건 조회
    //해당 카테고리의 이미지가 없을시 기본 이미지 반환
    @GetMapping("/all/{category}")
    public BaseResponse<ImageMultiResponseDto> getAllImage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                           @PathVariable String category,
                                                           @RequestBody ImageMultiRequestDto requestDto) {
        return BaseResponse.ok(imageService.getAllImage(userDetails.getUser().getId(),category, requestDto.getCategoryid()), BaseStatus.OK);
    }

    //이미지 단건 수정
    @PutMapping(path = "/{category}",consumes = "multipart/form-data")
    public BaseResponse<String> updateImage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @PathVariable String category,
                                            @RequestPart("file") MultipartFile file,
                                            @RequestPart("request") ImageRequestDto requestDto) {
        return BaseResponse.ok(imageService.updateImage(userDetails.getUser().getId(),category, requestDto, file), BaseStatus.OK);
    }

    //이미지 다건 수정
    @PutMapping("/all")
    public BaseResponse<ImageMultiResponseDto> updateAllImage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @RequestPart("files") List<MultipartFile> files,
                                                              @RequestPart("request") String request) {
        return BaseResponse.ok(imageService.updateAllImage(userDetails.getUser().getId(), request, files), BaseStatus.OK);
    }

    //이미지 인덱스 바꿈
//    @PutMapping("/{category}/index")
//    public BaseResponse<ImageResponseDto> changeImageIndex(@AuthenticationPrincipal UserDetailsImpl userDetails,
//                                                           @PathVariable String category,
//                                                           @RequestBody ImageIndexRequestDto requestDto) {
//        return BaseResponse.ok(imageService.changeImageIndex(userDetails.getUser().getId(),category, requestDto),BaseStatus.OK);
//    }


    //이미지 삭제
//    @DeleteMapping("/{category}")
//    public BaseResponse<Void> deleteImage(@AuthenticationPrincipal UserDetailsImpl userDetails,
//                                          @PathVariable String category,
//                                          @RequestBody ImageRequestDto requestDto) {
//        imageService.deleteImage(userDetails.getUser().getId(),category, requestDto);
//        return BaseResponse.ok(BaseStatus.OK);
//    }

    //이미지 다건 삭제
//    @DeleteMapping("/all/{category}")
//    public BaseResponse<Void> deleteAllImage(@AuthenticationPrincipal UserDetailsImpl userDetails,
//                                             @PathVariable String category,
//                                             @RequestBody ImageMultiRequestDto requestDto) {
//        imageService.deleteAllImage(userDetails.getUser().getId(),category, requestDto.getCategoryid());
//        return BaseResponse.ok(BaseStatus.OK);
//    }
}
