package com.meetkey.server.global.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // S3에 이미지 업로드용 Presigned url 발급
    public S3ResDTO.PresignedUrl generateUploadPresignedUrl(String folder, String originalFileName, String contentType){
        String key = folder + "/" + UUID.randomUUID() +"-" + originalFileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(
                r -> r.putObjectRequest(putObjectRequest)
                        .signatureDuration(Duration.ofMinutes(5))
        );

        return S3ResDTO.PresignedUrl.builder()
                .url(presignedPutObjectRequest.url().toString())
                .key(key).build();
    }

    // S3 이미지 조회용 Presigned url 발급
    public String generateGetPresignedUrl(String key){
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }

    // s3에서 이미지 삭제
    public void deleteFile(String imageUrl){
        String key = extractKeyFromUrl(imageUrl);

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()
        );
    }

    // s3 url에서 key 추출
    private String extractKeyFromUrl(String imageUrl){
        int idx = imageUrl.indexOf(".amazonaws.com/") +  ".amazonaws.com/".length();
        return imageUrl.substring(idx);
    }
}
