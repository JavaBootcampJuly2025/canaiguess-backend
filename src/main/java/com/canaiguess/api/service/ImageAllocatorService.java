package com.canaiguess.api.service;

import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.Image;
import com.canaiguess.api.model.ImageGame;
import com.canaiguess.api.repository.ImageGameRepository;
import com.canaiguess.api.repository.ImageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ImageAllocatorService {

    private final ImageRepository imageRepository;
    private final ImageGameRepository imageGameRepository;

    public ImageAllocatorService(ImageRepository imageRepository,
                            ImageGameRepository imageGameRepository) {

        this.imageRepository = imageRepository;
        this.imageGameRepository = imageGameRepository;
    }

    public void allocateImagesForGame(Game game) {
        // we want pairs of (real, fake) for batchSize of two
        if (game.getBatchSize() == 2) {
            allocateTwoImageBatch(game);
            return;
        }

        int totalNeeded = game.getBatchCount() * game.getBatchSize();
        double targetDifficulty = game.getDifficulty() / 100.0; // assume 0-100 input

        // as many as we can from unplayed pool
        List<Image> fresh = imageRepository.findFreshImages(PageRequest.of(0, totalNeeded));
        List<Image> selected = new ArrayList<>(fresh);

        // if not enough, fill with ones played by others
        if (selected.size() < totalNeeded) {
            int remaining = totalNeeded - selected.size();
            List<Image> playedByOthers = imageRepository.findPlayedByOthersSortedByDifficulty(
                    game.getUser(),
                    targetDifficulty,
                    PageRequest.of(0, remaining)
            );
            selected.addAll(playedByOthers);
        }

        // if still not enough, fill with already played
        if (selected.size() < totalNeeded) {
            int remaining = totalNeeded - selected.size();
            List<Image> seenByUser = imageRepository.findPlayedByUserSortedByDifficulty(
                    game.getUser(),
                    targetDifficulty,
                    PageRequest.of(0, remaining)
            );
            selected.addAll(seenByUser);
        }

        // defensive check
        if (selected.size() < totalNeeded) {
            throw new IllegalStateException("Not enough images to create a game");
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

    public void allocateTwoImageBatch(Game game) {
        int batchCount = game.getBatchCount();
        double targetDifficulty = game.getDifficulty() / 100.0;

        // Try to fetch fresh real/fake first
        List<Image> realImages = new ArrayList<>(imageRepository.findFreshByFakeness(false, PageRequest.of(0, batchCount)));
        List<Image> fakeImages = new ArrayList<>(imageRepository.findFreshByFakeness(true, PageRequest.of(0, batchCount)));

        // Played by others if not enough
        if (realImages.size() < batchCount) {
            int needed = batchCount - realImages.size();
            realImages.addAll(imageRepository.findPlayedByOthersByFakeness(
                    game.getUser(), targetDifficulty, false, PageRequest.of(0, needed)));
        }
        if (fakeImages.size() < batchCount) {
            int needed = batchCount - fakeImages.size();
            fakeImages.addAll(imageRepository.findPlayedByOthersByFakeness(
                    game.getUser(), targetDifficulty, true, PageRequest.of(0, needed)));
        }

        // Played by user if still not enough
        if (realImages.size() < batchCount) {
            int needed = batchCount - realImages.size();
            realImages.addAll(imageRepository.findPlayedByUserByFakeness(
                    game.getUser(), targetDifficulty, false, PageRequest.of(0, needed)));
        }
        if (fakeImages.size() < batchCount) {
            int needed = batchCount - fakeImages.size();
            fakeImages.addAll(imageRepository.findPlayedByUserByFakeness(
                    game.getUser(), targetDifficulty, true, PageRequest.of(0, needed)));
        }

        if (realImages.size() < batchCount || fakeImages.size() < batchCount) {
            throw new IllegalStateException("Not enough real/fake images to create a 2-image batch game");
        }

        Collections.shuffle(realImages);
        Collections.shuffle(fakeImages);

        for (int batch = 1; batch <= batchCount; batch++) {
            ImageGame ig1 = new ImageGame();
            ig1.setGame(game);
            ig1.setImage(realImages.get(batch - 1));
            ig1.setBatchNumber(batch);
            imageGameRepository.save(ig1);

            ImageGame ig2 = new ImageGame();
            ig2.setGame(game);
            ig2.setImage(fakeImages.get(batch - 1));
            ig2.setBatchNumber(batch);
            imageGameRepository.save(ig2);
        }
    }

}
