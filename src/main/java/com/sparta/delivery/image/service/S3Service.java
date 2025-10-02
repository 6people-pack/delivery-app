package com.sparta.delivery.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.sparta.delivery.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.sparta.delivery.global.exception.domain.ErrorCode;

import java.io.IOException;

@Component
@RequiredArgsConstructor
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
}
