package com.canaiguess.api.service;

import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.Image;
import com.canaiguess.api.model.ImageGame;
import com.canaiguess.api.repository.ImageGameRepository;
import com.canaiguess.api.repository.ImageRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageGameService {

    private final ImageRepository imageRepository;
    private final ImageGameRepository imageGameRepository;

    public ImageGameService(ImageRepository imageRepository, ImageGameRepository imageGameRepository) {
        this.imageRepository = imageRepository;
        this.imageGameRepository = imageGameRepository;
    }

    public void allocateImagesForGame(Game game) {
        int totalNeeded = game.getBatchCount() * game.getBatchSize();
        double targetDifficulty = game.getDifficulty() / 100.0; // assume 0-100 input

        // unplayed images for this user
        List<Image> unplayed = imageRepository.findUnplayedImagesByUser(game.getUserId());

        // rank by distance to target difficulty
        List<Image> sortedByDifficulty = unplayed.stream()
                .filter(img -> img.getTotal_guesses() > 0)
                .sorted((a, b) -> {
                    double da = 1.0 - (a.getCorrect_guesses() / (double) a.getTotal_guesses());
                    double db = 1.0 - (b.getCorrect_guesses() / (double) b.getTotal_guesses());
                    return Double.compare(Math.abs(da - targetDifficulty), Math.abs(db - targetDifficulty));
                })
                .toList();

        // as many as we can from unplayed pool
        List<Image> selected = sortedByDifficulty.stream()
                .limit(totalNeeded)
                .collect(Collectors.toList());

        // if not enough, fill with previously played
        if (selected.size() < totalNeeded) {
            List<Image> allImages = imageRepository.findAll();
            allImages.removeAll(selected); // avoid duplicates

            List<Image> remaining = allImages.stream()
                    .filter(img -> img.getTotal_guesses() > 0)
                    .sorted((a, b) -> {
                        double da = 1.0 - (a.getCorrect_guesses() / (double) a.getTotal_guesses());
                        double db = 1.0 - (b.getCorrect_guesses() / (double) b.getTotal_guesses());
                        return Double.compare(Math.abs(da - targetDifficulty), Math.abs(db - targetDifficulty));
                    })
                    .limit(totalNeeded - selected.size())
                    .toList();

            selected.addAll(remaining);
        }

        // if still not enough, take also images played by no one
        if (selected.size() < totalNeeded) {
            List<Image> all = imageRepository.findAll();
            all.removeAll(selected);
            Collections.shuffle(all);
            selected.addAll(all.stream()
                    .limit(totalNeeded - selected.size())
                    .toList());
        }

        // Defensive check
        if (selected.size() < totalNeeded) {
            throw new IllegalStateException("Not enough images in DB to create a game");
        }

        // shuffle and assign into batches
        Collections.shuffle(selected);
        int index = 0;

        for (int batch = 0; batch < game.getBatchCount(); batch++) {
            for (int i = 0; i < game.getBatchSize(); i++) {
                Image image = selected.get(index++);
                ImageGame ig = new ImageGame();
                ig.setGame(game);
                ig.setImage(image);
                ig.setBatchNumber(batch);
                imageGameRepository.save(ig);
            }
        }
    }

}

