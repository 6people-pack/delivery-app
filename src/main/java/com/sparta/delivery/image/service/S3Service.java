package com.sparta.delivery.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.sparta.delivery.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadImage(String category, String categoryId, MultipartFile file, String imageId) {
        try{
            String fileName = category +"/"+ categoryId +"/"+ imageId+file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), objectMetadata);
            return amazonS3Client.getUrl(bucket, fileName).toString();
        }catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    public void deleteImage(String imageUrl) {
        try {
            String fileName = imageUrl.substring(imageUrl.lastIndexOf(".com/") + 5);
            amazonS3Client.deleteObject(bucket, fileName);
        }catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_DELETE_ERROR);
        }
    }

    public void deleteFolder(String category, String categoryId) {
        String prefix = category + "/" + categoryId + "/";

        // S3에서 해당 폴더의 객체들을 모두 가져옵니다.
        List<KeyVersion> objectsToDelete = new ArrayList<>();
        ListObjectsV2Result result = amazonS3Client.listObjectsV2(bucket, prefix);

        for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
            objectsToDelete.add(new KeyVersion(objectSummary.getKey()));
        }

        //객체가 없는 경우 예외 처리
        if(objectsToDelete.isEmpty()) throw new BusinessException(ErrorCode.S3_FOLDER_NO_FILE);

        // 객체가 있는 경우에만 일괄 삭제를 실행합니다.
        DeleteObjectsRequest request = new DeleteObjectsRequest(bucket)
                .withKeys(objectsToDelete);
        DeleteObjectsResult deleteObjectsResult = amazonS3Client.deleteObjects(request);
        // 삭제 결과 확인 (필요한 경우)
        log.info("Deleted objects: " + prefix + " , size : " + deleteObjectsResult.getDeletedObjects().size());
    }
}
