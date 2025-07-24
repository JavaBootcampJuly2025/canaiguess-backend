package com.canaiguess.api.service;

import com.canaiguess.api.model.Image;
import com.canaiguess.api.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public void softDeleteImage(String publicId) {
        Image image = imageRepository.findByPublicIdAndDeletedFalse(publicId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found or already deleted"));

        image.setDeleted(true);
        imageRepository.save(image);
    }

    public void undeleteImage(String publicId) {
        Image image = imageRepository.findByPublicId(publicId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found"));

        image.setDeleted(false);
        imageRepository.save(image);
    }

    public List<Image> getAllSoftDeletedImages() {
        return imageRepository.findAllDeleted();
    }
}
