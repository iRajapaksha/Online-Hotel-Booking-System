package com.irajapaksha.hotel_service.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;

@Service

public class S3Service {
    @Value("${aws.region}")
    private String region;
    private final S3Presigner presigner;
    private final String bucket;

    public S3Service(
            S3Presigner presigner,
            @Value("${aws.s3.bucket}") String bucket
    ) {
        this.presigner = presigner;
        this.bucket = bucket;
    }


    // Generate a presigned PUT URL (valid for `expirySeconds`)
    public String generatePresignedPutUrl(String key, long expirySeconds) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(expirySeconds))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString();
    }



    public String buildPublicUrl(String key) {
        try {
            String encodedKey = URLEncoder.encode(key, "UTF-8");
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, encodedKey);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error encoding S3 key", e);
        }
    }
}