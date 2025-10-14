package com.sparta.delivery.image.service;

import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.image.domain.Category;
import com.sparta.delivery.image.domain.Image;
import com.sparta.delivery.image.dto.ImageMultiResponseDto;
import com.sparta.delivery.image.dto.ImageRequestDto;
import com.sparta.delivery.image.dto.ImageSimpleResponseDto;
import com.sparta.delivery.image.repository.ImageRepository;
import com.sparta.delivery.menu.repository.MenuRepository;
import com.sparta.delivery.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.user.domain.User;
import com.sparta.delivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {
    private final ImageRepository imageRepository;
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;

    private final int MAX_IMAGE_COUNT = 10;

    //이미지는 식당, 메뉴, 리부에 각각 업로드 가능
    //이미지
//    public void ImageToRestaurant(Long userId, String category, String categoryid, List<MultipartFile> files) {
//        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
//        Restaurant restaurant = restaurantRepository.findById(UUID.fromString(categoryid)).orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
//
//    }



    //이미지 최초 업로드(다수 가능)
    @Transactional
    public void uploadImage(Category category, UUID categoryid, List<MultipartFile> files) {
        //이미지는 한 폴더당 10개 제한
        if(files.size() > MAX_IMAGE_COUNT) throw new BusinessException(ErrorCode.IMAGE_MAX_COUNT);

        int count = 0;
        for(MultipartFile file : files) {
            UUID imageId = UUID.randomUUID();
            String imageUrl = s3Service.uploadImage(category.toString(), categoryid.toString(), file, imageId.toString());
            Image image = new Image(imageId, category, categoryid, imageUrl, count+1);
            imageRepository.save(image);
            count++;
        }
    }

    //이미지 단건 조회
//    public String getImage(Long userId, String category, ImageRequestDto requestDto) {
//        //권한 체크
//        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
//        if(!Category.isPresent(category)) throw new BusinessException(ErrorCode.INVALID_CATEGORY);
//        // 해당 카테고리의 객체가 있는지 확인
//        Image image = imageRepository.findByCategoryAndCategoryIdAndIndex(Category.valueOf(category), UUID.fromString(requestDto.getCategoryid()), requestDto.getIndex())
//                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));
//        return image.getUrl();
//    }

    //이미지 다건 조회 - 권한 체크 x
    public ImageMultiResponseDto getAllImage(Long userId, String category, String categoryid) {
        //해당 카테고리의 객체가 있는지 확인
        List<Image> images = imageRepository.findAllByCategoryAndCategoryIdOrderByIndexAsc(Category.valueOf(category), UUID.fromString(categoryid));
        if(images.isEmpty()) {
            if(Category.valueOf(category) == Category.review) return new ImageMultiResponseDto(category, categoryid, new ArrayList<>());
            else return new ImageMultiResponseDto(category, categoryid, List.of(new ImageSimpleResponseDto("resources/static/no_food.png",1)));
        }
        List<ImageSimpleResponseDto> imageList = images.stream()
                .map(image -> new ImageSimpleResponseDto(image.getUrl(), image.getIndex()))
                .toList();
        return new ImageMultiResponseDto(category, categoryid, imageList);
    }

    // 이미지 수정 : 기존의 이미지를 지우고 다른 이미지 생성
    @Transactional
    public String updateImage(Long userId, String category, ImageRequestDto requestDto , MultipartFile file) {
        //권한 체크
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if(!Category.isPresent(category)) throw new BusinessException(ErrorCode.INVALID_CATEGORY);
        // 해당 카테고리의 객체가 있는지 확인
        Image image = imageRepository.findByCategoryAndCategoryIdAndIndex(Category.valueOf(category), UUID.fromString(requestDto.getCategoryid()), requestDto.getIndex())
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));

        s3Service.deleteImage(image.getUrl());
        String imageUrl = s3Service.uploadImage(category, requestDto.getCategoryid(), file, image.getId().toString());
        log.info("Updated image URL: {}", imageUrl);
        return imageUrl;
    }

    //이미지 다건 수정
    @Transactional
    public ImageMultiResponseDto updateAllImage(Long userId,String requestDto , List<MultipartFile> files) {
//        JSONObject
//        Category category = Category.valueOf(category);
//        switch (cat) {
//            case restaurant -> {
//                userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
//                Restaurant restaurant = restaurantRepository.findById(UUID.fromString(requestDto.getCategoryid())).orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
//                if(!restaurant.getOwnerId().equals(userId)) throw new BusinessException(ErrorCode.NOT_OWNER);
//            }
//            case menu -> {
//                userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
//                Menu menu = menuRepository.findById(UUID.fromString(requestDto.getCategoryid())).orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
//                if (!restaurant.getOwner().getId().equals(userId))
//                    throw new BusinessException(ErrorCode.INVALID_PERMISSION);
//            }
//            case review -> {
//                //리뷰는 권한 체크 x
//            }
//            default -> throw new BusinessException(ErrorCode.INVALID_CATEGORY);
//        }
        return null;
    }

    //이미지 순서 조정(인덱스 변경)
//    @Transactional
//    public ImageResponseDto changeImageIndex(Long userId, String category, ImageIndexRequestDto requestDto) {
//        //권한 체크
//        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
//        if(!Category.isPresent(category)) throw new BusinessException(ErrorCode.INVALID_CATEGORY);
//        // 해당 카테고리의 객체가 있는지 확인
//        Image image = imageRepository.findByCategoryAndCategoryIdAndIndex(Category.valueOf(category), UUID.fromString(requestDto.getCategoryid()), requestDto.getIndex())
//                .orElseThrow(()->new BusinessException(ErrorCode.IMAGE_NOT_FOUND));
//        //변경할 인덱스가 기존의 인덱스와 같은지 확인
//        if(requestDto.getNewIndex()==requestDto.getIndex())throw new BusinessException(ErrorCode.IMAGE_SAME_INDEX);
//        //현재 인덱스가 변경할 인덱스보다 클때 - 사진을 앞으로 이동
//        else if(requestDto.getIndex() > requestDto.getNewIndex()) {
//            List<Image> images = imageRepository.findAllByCategoryAndCategoryIdAndIndexBetween(Category.valueOf(category), UUID.fromString(requestDto.getCategoryid()), requestDto.getNewIndex(), requestDto.getIndex()-1);
//            images.forEach(Image::increaseIndex);
//            image.updateIndex(requestDto.getNewIndex());
//        }
//        //현재 인덱스가 변경할 인덱스보다 작을때 - 사진을 뒤로 이동
//        else {
//            List<Image> images = imageRepository.findAllByCategoryAndCategoryIdAndIndexBetween(Category.valueOf(category), UUID.fromString(requestDto.getCategoryid()), requestDto.getIndex()+1, requestDto.getNewIndex());
//            images.forEach(Image::decreaseIndex);
//            image.updateIndex(requestDto.getNewIndex());
//        }
//        return new ImageResponseDto(image);
//    }

    //이미지 삭제
//    @Transactional
//    public void deleteImage(Long userId, String category, ImageRequestDto requestDto) {
//        //권한 체크
//        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
//        if(!Category.isPresent(category)) throw new BusinessException(ErrorCode.INVALID_CATEGORY);
//        // 해당 카테고리의 객체가 있는지 확인
//        Image image = imageRepository.findByCategoryAndCategoryIdAndIndex(Category.valueOf(category), UUID.fromString(requestDto.getCategoryid()), requestDto.getIndex())
//                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));
//        s3Service.deleteImage(image.getUrl());
//        imageRepository.delete(image);
//        List<Image> images = imageRepository.findAllByCategoryAndCategoryIdAndIndexAfter(Category.valueOf(category), UUID.fromString(requestDto.getCategoryid()), requestDto.getIndex());
//        images.forEach(Image::decreaseIndex);
//    }

    //이미지 카테고리 삭제
    @Transactional
    public void deleteAllImage(Category category, UUID categoryid) {

        //해당 카테고리에 이미지가 하나도 없을시 종료
        if(imageRepository.countByCategoryAndCategoryId(category, categoryid)==0)  return;

        //해당 카테고리의 이미지를 데이터와 s3에서 모두 삭제
        imageRepository.deleteAllByCategoryAndCategoryId(category, categoryid);
        s3Service.deleteFolder(category.toString(), categoryid.toString());
    }
}
