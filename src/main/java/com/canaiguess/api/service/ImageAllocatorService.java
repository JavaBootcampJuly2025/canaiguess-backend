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
public class ImageAllocatorService {

    private final ImageRepository imageRepository;
    private final ImageGameRepository imageGameRepository;

    public ImageAllocatorService(ImageRepository imageRepository,
                            ImageGameRepository imageGameRepository
                            ) {

        this.imageRepository = imageRepository;
        this.imageGameRepository = imageGameRepository;
    }

    public void allocateImagesForGame(Game game) {
        int totalNeeded = game.getBatchCount() * game.getBatchSize();
        double targetDifficulty = game.getDifficulty() / 100.0; // assume 0-100 input

        // unplayed images for this user
        List<Image> unplayed = imageRepository.findUnplayedImagesByUser(game.getUser());

        // fresh images (never played by anyone)
        List<Image> neverPlayedByAnyone = unplayed.stream()
                .filter(img -> img.getTotal() == 0)
                .toList();

        // played ones (played by at least someone),
        // ranked by distance to target difficulty
        List<Image> playedByOthers = unplayed.stream()
                .filter(img -> img.getTotal() > 0)
                .sorted((a, b) -> {
                    double da = 1.0 - (a.getCorrect() / (double) a.getTotal());
                    double db = 1.0 - (b.getCorrect() / (double) b.getTotal());
                    return Double.compare(Math.abs(da - targetDifficulty), Math.abs(db - targetDifficulty));
                })
                .toList();

        // these will be allocated for the game;
        // as many as we can from unplayed pool
        List<Image> selected = neverPlayedByAnyone.stream()
                .limit(totalNeeded).collect(Collectors.toList());

        // if not enough, fill with ones played by someone,
        // by difficulty sorted images
        if (selected.size() < totalNeeded) {
            int remaining = totalNeeded - selected.size();
            selected.addAll(playedByOthers.stream()
                    .limit(remaining)
                    .toList());
        }

        // if still not enough, look through all images,
        // also previously played by the user
        if (selected.size() < totalNeeded) {
            List<Image> allImages = imageRepository.findAll();
            allImages.removeAll(selected);

            List<Image> previouslyPlayed = allImages.stream()
                    .filter(img -> img.getTotal() > 0)
                    .sorted((a, b) -> {
                        double da = 1.0 - (a.getCorrect() / (double) a.getTotal());
                        double db = 1.0 - (b.getCorrect() / (double) b.getTotal());
                        return Double.compare(Math.abs(da - targetDifficulty), Math.abs(db - targetDifficulty));
                    })
                    .limit(totalNeeded - selected.size())
                    .toList();

            selected.addAll(previouslyPlayed);
        }

        // Defensive check
        if (selected.size() < totalNeeded) {
            throw new IllegalStateException("Not enough images in DB to create a game");
        }

        // shuffle and assign into batches
        Collections.shuffle(selected);
        int index = 0;

        for (int batch = 1; batch <= game.getBatchCount(); batch++) {
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
