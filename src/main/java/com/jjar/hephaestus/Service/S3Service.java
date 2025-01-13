package com.jjar.hephaestus.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class S3Service {

    private S3Client s3Client;

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private String resolvedBucketName;

    @PostConstruct
    private void initializeS3Client() {
        this.resolvedBucketName = bucketName;
        this.s3Client = S3Client.builder()
                .region(Region.of(region)) // Dynamically load region
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey) // Load credentials
                        )
                )
                .build();
    }

    public String uploadFile(String key, String content) {
        try (InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(resolvedBucketName)
                            .key(key)
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, content.length())
            );
            return key; // Return the object key for tracking
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during S3 upload", e);
        }
    }

    public String getFileContent(String key) {
        try {
            InputStream inputStream = s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(resolvedBucketName)
                            .key(key)
                            .build()
            );
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch file content", e);
        }
    }

}
