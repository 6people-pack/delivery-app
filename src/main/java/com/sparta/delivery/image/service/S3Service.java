package com.sparta.delivery.image.service;

import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URL;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client amazonS3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadImage(String category, String categoryId, MultipartFile file, String imageId) {
        try{
            String fileName = category +"/"+ categoryId +"/"+ imageId+file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    // .contentLength(file.getSize()) // 필요 시 설정 가능 (RequestBody에 길이 전달로 충분)
                    .build();

            amazonS3Client.putObject(putReq, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            URL url = amazonS3Client.utilities().getUrl(GetUrlRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build());
            log.info("File uploaded to S3: {}", fileName);

            return url.toString();
        } catch (Exception e) {
            log.warn("Failed to upload file to S3", e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    public void deleteFolder(String category, String categoryId) {
        try {
            String prefix = category + "/" + categoryId + "/";

            ListObjectsV2Response listRes = amazonS3Client.listObjectsV2(
                    ListObjectsV2Request.builder()
                            .bucket(bucket)
                            .prefix(prefix)
                            .build()
            );

            List<ObjectIdentifier> toDelete = listRes.contents().stream()
                    .map(obj -> ObjectIdentifier.builder().key(obj.key()).build())
                    .toList();

            //해당 폴더가 없는 경우
            if (toDelete.isEmpty()) {
                throw new BusinessException(ErrorCode.S3_FOLDER_NO_FILE);
            }

            Delete del = Delete.builder().objects(toDelete).build();

            amazonS3Client.deleteObjects(DeleteObjectsRequest.builder()
                    .bucket(bucket)
                    .delete(del)
                    .build());

            log.info("Deleted {} objects under prefix '{}'", toDelete.size(), prefix);
        } catch (Exception e) {
            log.warn("Failed to delete folder in S3", e);
            throw new BusinessException(ErrorCode.FILE_DELETE_ERROR);
        }
    }

    public void deleteImages(List<String> urls) {
        if (urls.isEmpty()) return;

        List<ObjectIdentifier> objects = urls.stream()
                .map(url -> ObjectIdentifier.builder()
                        .key(url.substring(url.lastIndexOf(".com/") + 17))
                        .build()
                )
                .toList();
        try {
            DeleteObjectsResponse res = amazonS3Client.deleteObjects(
                    DeleteObjectsRequest.builder()
                            .bucket(bucket)
                            .delete(Delete.builder().objects(objects).build())
                            .build()
            );

            // 성공한 항목 목록 확인 가능
            List<DeletedObject> deleted = res.deleted();
            log.info("Deleted {} objects from S3.", deleted.size());
            log.info("delete errors: {}", res.errors());
        } catch (Exception e) {
            log.warn("Failed to delete images in S3", e);
            throw new BusinessException(ErrorCode.FILE_DELETE_ERROR);
        }
    }

}
