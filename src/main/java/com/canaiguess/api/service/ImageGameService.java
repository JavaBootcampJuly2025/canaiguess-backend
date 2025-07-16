package com.canaiguess.api.service;

import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.Image;
import com.canaiguess.api.model.ImageGame;
import com.canaiguess.api.repository.ImageGameRepository;
import com.canaiguess.api.repository.ImageRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ImageGameService {

    private final ImageRepository imageRepository;
    private final ImageGameRepository imageGameRepository;

    public ImageGameService(ImageRepository imageRepository, ImageGameRepository imageGameRepository) {
        this.imageRepository = imageRepository;
        this.imageGameRepository = imageGameRepository;
    }

    public void allocateImagesForGame(Game game) {
        int batches = game.getBatchCount();
        int imagesPerBatch = game.getBatchSize();
        List<Image> pool = imageRepository.findAll();

        // TODO: Shuffle and allocate images into batches
        Collections.shuffle(pool);
        int index = 0;

        for (int batch = 0; batch < batches; batch++) {
            for (int i = 0; i < imagesPerBatch; i++) {
                Image image = pool.get(index++);
                ImageGame ig = new ImageGame();
                ig.setGame(game);
                ig.setImage(image);
                ig.setBatchNumber(batch);
                imageGameRepository.save(ig);
            }
        }
    }
}

