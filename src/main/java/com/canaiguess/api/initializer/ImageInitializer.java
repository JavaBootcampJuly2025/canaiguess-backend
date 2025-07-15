package com.canaiguess.api.initializer;

import com.canaiguess.api.model.Image;
import com.canaiguess.api.repository.ImageRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ImageInitializer implements CommandLineRunner {

    private final ImageRepository imageRepository;

    public ImageInitializer(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (imageRepository.count() > 0) {
            return; // Skip if images already exist
        }

        List<Image> images = new ArrayList<>();

        for (int i = 0; i < 2000; i++) {
            Image img = new Image();
            img.setFilename(String.format("%04d.jpg", i));
            img.setImage_type(i < 1000); // true = AI, false = real
            img.setTotal_guesses(0);
            img.setCorrect_guesses(0);
            images.add(img);
        }

        imageRepository.saveAll(images);
    }
}
