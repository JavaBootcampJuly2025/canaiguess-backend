package com.canaiguess.api.service;

import com.canaiguess.api.dto.ImageDTO;
import com.canaiguess.api.model.Image;
import com.canaiguess.api.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

    public Page<ImageDTO> getAllSoftDeletedImages(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return imageRepository.findAllByDeletedTrue(pageable)
                .map(ImageDTO::from);
    }

}
