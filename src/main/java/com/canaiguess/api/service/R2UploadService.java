package com.canaiguess.api.service;

import com.canaiguess.api.model.Image;
import com.canaiguess.api.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class R2UploadService {

    private final S3Client s3Client;
    private final ImageRepository imageRepository;

    @Value("${cloudflare.r2.bucket-name}")
    private String bucketName;

    public String uploadAndSave(MultipartFile file, boolean isFake) {
        String key = RandomStringUtils.randomAlphanumeric(8).toLowerCase();

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }

        String url = "https://pub-0b1ff2b3a44542d9b41eee3972174601.r2.dev/" + key;

        Image image = new Image();
        image.setUrl(url);
        image.setPublicId(key);
        image.setFake(isFake);
        image.setTotal(0);
        image.setCorrect(0);
        imageRepository.save(image);

        return url;
    }
}
