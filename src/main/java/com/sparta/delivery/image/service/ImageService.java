package com.sparta.delivery.image.service;

import com.sparta.delivery.global.category.CategoryCheck;
import com.sparta.delivery.global.category.ImageCategory;
import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.image.domain.Image;
import com.sparta.delivery.image.dto.*;
import com.sparta.delivery.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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
    private final CategoryCheck categoryCheck;

    private final int MAX_IMAGE_COUNT = 10;


    //이미지 최초 업로드(다수 가능)
    @Transactional
    public void uploadImage(ImageCategory imageCategory, UUID categoryId, List<MultipartFile> files) {
        //이미지는 한 폴더당 10개 제한
        if(files.size() > MAX_IMAGE_COUNT) throw new BusinessException(ErrorCode.IMAGE_MAX_COUNT);

        int count = 0;
        for(MultipartFile file : files) {
            UUID imageId = UUID.randomUUID();
            String imageUrl = s3Service.uploadImage(imageCategory.toString(), categoryId.toString(), file, imageId.toString());
            Image image = new Image(imageId, imageCategory, categoryId, imageUrl, count+1);
            imageRepository.save(image);
            count++;
        }
    }

    //이미지 다건 조회 - 권한 체크 x
    public ImageMultiResponseDto getAllImage( String category, String categoryid) {
        //해당 카테고리의 객체가 있는지 확인
        List<Image> images = imageRepository.findAllByCategoryAndCategoryIdOrderByIndexAsc(ImageCategory.valueOf(category), UUID.fromString(categoryid));
        if(images.isEmpty()) {
            if(ImageCategory.valueOf(category) == ImageCategory.review) return new ImageMultiResponseDto(category, categoryid, new ArrayList<>());
            else return new ImageMultiResponseDto(category, categoryid, List.of(new ImageSimpleResponseDto("resources/static/no_food.png",1)));
        }
        List<ImageSimpleResponseDto> imageList = images.stream()
                .map(image -> new ImageSimpleResponseDto(image.getUrl(), image.getIndex()))
                .toList();
        return new ImageMultiResponseDto(category, categoryid, imageList);
    }

    //이미지 다건 수정
    //todo : s3 업로드, 삭제 실패시 결과도 확인
    //todo : 메서드 분리하기
    //todo : menu에서 restaurant를 가져오는 방법 바뀐 것 확인하기
    @Transactional
    public ImageMultiResponseDto updateAllImage(Long userId, ImageUpdateRequestDto requestDto , List<MultipartFile> files) {
        ImageCategory imageCategory = ImageCategory.valueOf(requestDto.getCategory());
        UUID categoryid = UUID.fromString(requestDto.getCategoryId());

        //권한 체크
        categoryCheck.checkAuthority(userId, imageCategory, categoryid);

        //기존에 남아있는 이미지와 새로 업로드할 이미지의 합이 10개를 넘지 않는지 확인
        if(requestDto.getUpdate().size() + requestDto.getCreate().size() > MAX_IMAGE_COUNT)
            throw new BusinessException(ErrorCode.IMAGE_MAX_COUNT);

        //이미지 삭제
        List<String> urls = requestDto.getDelete().stream().map(ImageDDto::getUrl).toList();
        List<UUID> ids = urls.stream().map(url ->UUID.fromString(url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".")))).toList();
        List<Image> dImages = imageRepository.findAllById(ids);

        //받은 url개수와 찾은 이미지 개수가 다르면 이미지를 못찾은 것, 제대로 찾았다면 데이터 삭제
        if(dImages.size() != ids.size()) throw new BusinessException(ErrorCode.IMAGE_NOT_FOUND);
        imageRepository.deleteAll(dImages);

        //커밋에 성공시 s3에서 이미지 삭제
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override public void afterCommit() {
                s3Service.deleteImages(urls);
            }
        });


        //기존 이미지 인덱스 수정
        List<ImageUDto> update = requestDto.getUpdate();
        for (ImageUDto imageUDto : update) {
            Image image = imageRepository.findById(UUID.fromString(imageUDto.getImageId()))
                    .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));
            image.updateIndex(imageUDto.getIndex());
        }

        //새로운 이미지 업로드
        List<ImageCDto> create = requestDto.getCreate();
        List<Image> cImages = new ArrayList<>(create.size());
        List<String> cUrls = new ArrayList<>(create.size());
        //받은 인덱스 개수와 파일 개수가 다르면 오류
        if(create.size() != files.size()) throw new BusinessException(ErrorCode.MISMATCHED_IMAGE_COUNT);
        for(int i = 0; i < create.size(); i++) {
            UUID imageId = UUID.randomUUID();
            //s3업로드
            String imageUrl = s3Service.uploadImage(imageCategory.toString(), categoryid.toString(), files.get(i), imageId.toString());
            cUrls.add(imageUrl);
            cImages.add(new Image(imageId, imageCategory, categoryid, imageUrl, create.get(i).getIndex()));
        }
        //db업로드
        imageRepository.saveAll(cImages);

        //트랜잭션 롤백시 방금 올린 S3들을 보상 삭제
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    try {
                        s3Service.deleteImages(cUrls);
                    }catch (Exception e) {
                        //보상 삭제 실패시 로그 남기기
                        log.error("S3 cleanup failed after rollback: {}", cUrls, e);
                    }
                }
            }
        });

        //인덱스 맞는지 확인
        List<Image> images = imageRepository.findAllByCategoryAndCategoryIdOrderByIndexAsc(imageCategory, categoryid);
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).getIndex() != i + 1) throw new BusinessException(ErrorCode.NOT_SEQUENTIAL_INDEX);
        }
        return new ImageMultiResponseDto(imageCategory.toString(), categoryid.toString(),
                images.stream().map(image -> new ImageSimpleResponseDto(image.getUrl(), image.getIndex())).toList());
    }

    //이미지 카테고리 삭제
    @Transactional
    public void deleteAllImage(ImageCategory imageCategory, UUID categoryId) {

        //해당 카테고리에 이미지가 하나도 없을시 종료
        if(imageRepository.countByCategoryAndCategoryId(imageCategory, categoryId)==0)  return;

        //해당 카테고리의 이미지를 데이터와 s3에서 모두 삭제
        imageRepository.deleteAllByCategoryAndCategoryId(imageCategory, categoryId);
        s3Service.deleteFolder(imageCategory.toString(), categoryId.toString());
    }

}
