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

import java.time.Duration;

@Service

public class S3Service {

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
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(expirySeconds))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString();
    }


    // Build the public URL for accessing the object
//    public String buildPublicUrl(String key) {
//        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, s3Client.region().id(), key);
//    }
}