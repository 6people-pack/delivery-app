package com.sparta.delivery.image.service;

import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.image.domain.Category;
import com.sparta.delivery.image.domain.Image;
import com.sparta.delivery.image.dto.ImageMultiResponseDto;
import com.sparta.delivery.image.dto.ImageRequestDto;
import com.sparta.delivery.image.dto.ImageSimpleResponseDto;
import com.sparta.delivery.image.repository.ImageRepository;
import com.sparta.delivery.menu.domain.Menu;
import com.sparta.delivery.menu.repository.MenuRepository;
import com.sparta.delivery.restaurant.domain.Restaurant;
import com.sparta.delivery.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.review.domain.Review;
import com.sparta.delivery.review.repository.ReviewRepository;
import com.sparta.delivery.user.domain.User;
import com.sparta.delivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
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
    private final ReviewRepository reviewRepository;

    private final int MAX_IMAGE_COUNT = 10;




    //이미지 업로드(다수 가능)
    @Transactional
    public ImageMultiResponseDto uploadImages(Long userId, String category, String categoryid, List<MultipartFile> files) {
        //권한 체크
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
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
    //todo : 오류 발생시 s3와 db 불일치 문제 해결
    @Transactional
    public ImageMultiResponseDto updateAllImage(Long userId,String requestDto , List<MultipartFile> files) {
        JSONObject jsonObject = new JSONObject(requestDto);
        Category category = Category.valueOf(jsonObject.getString("category"));
        UUID categoryid = UUID.fromString(jsonObject.getString("categoryId"));

        //권한 체크
        checkAuthority(userId, category, categoryid);

        //기존에 남아있는 이미지와 새로 업로드할 이미지의 합이 10개를 넘지 않는지 확인
        if(jsonObject.getJSONArray("update").length() + jsonObject.getJSONArray("create").length() > MAX_IMAGE_COUNT)
            throw new BusinessException(ErrorCode.IMAGE_MAX_COUNT);

        //이미지 삭제
        JSONArray delete = jsonObject.getJSONArray("delete");
        for(int i = 0; i < delete.length(); i++) {
            String url = delete.getJSONObject(i).getString("url");
            Image image = imageRepository.findById(UUID.fromString(url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."))))
                    .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));
            s3Service.deleteImage(url);
            imageRepository.delete(image);
        }

        //기존 이미지 인덱스 수정
        JSONArray update = jsonObject.getJSONArray("update");
        for(int i = 0; i < update.length(); i++) {
            Image image = imageRepository.findById(UUID.fromString(update.getJSONObject(i).getString("imageId")))
                    .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));
            image.updateIndex(update.getJSONObject(i).getInt("index"));
        }

        //새로운 이미지 업로드
        JSONArray create = jsonObject.getJSONArray("create");
        if(create.length() != files.size()) throw new BusinessException(ErrorCode.MISMATCHED_IMAGE_COUNT);
        for(int i = 0; i < create.length(); i++) {
            UUID imageId = UUID.randomUUID();
            //s3업로드
            String imageUrl = s3Service.uploadImage(category.toString(), categoryid.toString(), files.get(i), imageId.toString());
            //db업로드
            Image image = new Image(imageId, category, categoryid, imageUrl, create.getJSONObject(i).getInt("index"));
            imageRepository.save(image);
        }

        //인덱스 맞는지 확인
        List<Image> images = imageRepository.findAllByCategoryAndCategoryIdOrderByIndexAsc(category, categoryid);
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).getIndex() != i + 1) throw new BusinessException(ErrorCode.NOT_SEQUENTIAL_INDEX);
        }
        return new ImageMultiResponseDto(category.toString(), categoryid.toString(),
                images.stream().map(image -> new ImageSimpleResponseDto(image.getUrl(), image.getIndex())).toList());
    }

    private void checkAuthority(Long userId, Category category, UUID categoryid) {
        //권한 체크
        switch (category) {
            case restaurant -> {
                userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                Restaurant restaurant = restaurantRepository.findById(categoryid).orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
                if(!restaurant.getOwnerId().equals(userId)) throw new BusinessException(ErrorCode.NOT_OWNER);
            }
            case menu -> {
                userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                Menu menu = menuRepository.findById(categoryid).orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
                Restaurant restaurant = restaurantRepository.findById(menu.getRestaurant().getId()).orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
                if(!restaurant.getOwnerId().equals(userId)) throw new BusinessException(ErrorCode.NOT_OWNER);
            }
            case review -> {
                userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                Review review = reviewRepository.findById(categoryid).orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
                if(!review.getUserId().equals(userId)) throw new BusinessException(ErrorCode.NOT_REVIEWER);
            }
            default -> {}
        }
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
