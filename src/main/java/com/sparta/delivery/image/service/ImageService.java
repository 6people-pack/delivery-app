package com.sparta.delivery.image.service;

import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.image.domain.Category;
import com.sparta.delivery.image.domain.Image;
import com.sparta.delivery.image.dto.ImageMultiResponseDto;
import com.sparta.delivery.image.dto.ImageRequestDto;
import com.sparta.delivery.image.dto.ImageSimpleResponseDto;
import com.sparta.delivery.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {
    private final ImageRepository imageRepository;
    private final S3Service s3Service;



    @Transactional
    public ImageMultiResponseDto uploadImage(String category, String categoryid, List<MultipartFile> files) {
        if(!Category.isPresent(category)) throw new BusinessException(ErrorCode.INVALID_CATEGORY);
        // 해당 카테고리의 객체가 있는지 확인
        int count = imageRepository.countByCategoryAndCategoryId(Category.valueOf(category),UUID.fromString(categoryid));
        List<ImageSimpleResponseDto> imageList = new ArrayList<>();
        for(MultipartFile file : files) {
            UUID imageId = UUID.randomUUID();
            String imageUrl = s3Service.uploadImage(category, categoryid, file, imageId.toString());
            Image image = new Image(imageId, Category.valueOf(category), UUID.fromString(categoryid), imageUrl, count+1);
            imageRepository.save(image);
            imageList.add(new ImageSimpleResponseDto(imageId.toString(), image.getIndex()));
            count++;
        }
        return new ImageMultiResponseDto(category, categoryid, imageList);
    }

    public String getImage(String category, ImageRequestDto requestDto) {
        if(!Category.isPresent(category)) throw new BusinessException(ErrorCode.INVALID_CATEGORY);
        // 해당 카테고리의 객체가 있는지 확인
        Image image = imageRepository.findByCategoryAndCategoryIdAndIndex(Category.valueOf(category), UUID.fromString(requestDto.getCategoryid()), requestDto.getIndex())
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));
        return image.getUrl();
    }

    // 이미지 수정 : 기존의 이미지를 지우고 다른 이미지 생성
//    @Transactional
//    public String updateImage(String category, ImageRequestDto requestDto , MultipartFile file) {
//        if(!Category.isPresent(category)) throw new BusinessException(ErrorCode.INVALID_CATEGORY);
//        // 해당 카테고리의 객체가 있는지 확인
//        Image image = imageRepository.findByCategoryAndCategoryIdAndIndex(Category.valueOf(category), UUID.fromString(requestDto.getCategoryid()), requestDto.getIndex())
//                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));
//        image.changeindex();
//        String imageUrl = s3Service.uploadImage(category, requestDto.getCategoryid(), file);
//        Image newImage = new Image(Category.valueOf(category), UUID.fromString(requestDto.getCategoryid()), imageUrl, requestDto.getIndex());
//        imageRepository.save(newImage);
//        return imageUrl;
//    }

    //이미지 삭제
    @Transactional
    public void deleteImage(String category, ImageRequestDto requestDto) {
        if(!Category.isPresent(category)) throw new BusinessException(ErrorCode.INVALID_CATEGORY);
        // 해당 카테고리의 객체가 있는지 확인
        Image image = imageRepository.findByCategoryAndCategoryIdAndIndex(Category.valueOf(category), UUID.fromString(requestDto.getCategoryid()), requestDto.getIndex())
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));
        s3Service.deleteImage(image.getUrl());
        imageRepository.delete(image);
        int index = requestDto.getIndex();
        List<Image> images = imageRepository.findAllByCategoryAndCategoryIdOrderByIndexAsc(Category.valueOf(category), UUID.fromString(requestDto.getCategoryid()));
        for(int i = index-1; i < images.size(); i++){
            Image img = images.get(i);
            img.decreaseIndex();
            imageRepository.save(img);
        }
    }

    public ImageMultiResponseDto getAllImage(String category, String categoryid) {
        if(!Category.isPresent(category)) throw new BusinessException(ErrorCode.INVALID_CATEGORY);
        //해당 카테고리의 객체가 있는지 확인
        List<Image> images = imageRepository.findAllByCategoryAndCategoryIdOrderByIndexAsc(Category.valueOf(category), UUID.fromString(categoryid));
        List<ImageSimpleResponseDto> imageList = images.stream()
                .map(image -> new ImageSimpleResponseDto(image.getId().toString(), image.getIndex()))
                .toList();
        return new ImageMultiResponseDto(category, categoryid, imageList);
    }
}
