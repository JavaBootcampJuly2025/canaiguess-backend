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
        if (game.getBatchSize() == 2) {
            // we want pairs of (real, fake)
            allocateTwoImageBatch(game);
            return;
        }

        int totalNeeded = game.getBatchCount() * game.getBatchSize();
        double targetDifficulty = game.getDifficulty() / 100.0;

        // as many as we can from unplayed pool
        List<Image> selected = new ArrayList<>(imageRepository.findFreshImages(PageRequest.of(0, totalNeeded)));

        if (selected.size() < totalNeeded) {
            selected.addAll(fetchPlayedByOthers(game, totalNeeded - selected.size(), targetDifficulty));
        }

        if (selected.size() < totalNeeded) {
            selected.addAll(fetchPlayedByUser(game, totalNeeded - selected.size(), targetDifficulty));
        }

        // all images should have fallen within the previous three groups
        if (selected.size() < totalNeeded) {
            throw new IllegalStateException("Not enough images to create a game.");
        }

        Collections.shuffle(selected);
        int index = 0;

        for (int batch = 1; batch <= game.getBatchCount(); batch++) {
            for (int i = 0; i < game.getBatchSize(); i++) {
                imageGameRepository.save(new ImageGame(game, selected.get(index++), batch));
            }
        }
    }

    private List<Image> fetchPlayedByOthers(Game game, int remaining, double difficulty) {
        return imageRepository.findPlayedByOthersSortedByDifficulty(
                game.getUser(), difficulty, PageRequest.of(0, remaining));
    }

    private List<Image> fetchPlayedByUser(Game game, int remaining, double difficulty) {
        return imageRepository.findPlayedByUserSortedByDifficulty(
                game.getUser(), difficulty, PageRequest.of(0, remaining));
    }

    public void allocateTwoImageBatch(Game game) {
        int batchCount = game.getBatchCount();
        double targetDifficulty = game.getDifficulty() / 100.0;

        List<Image> realImages = fetchImagesByFakeness(game, false, batchCount, targetDifficulty);
        List<Image> fakeImages = fetchImagesByFakeness(game, true, batchCount, targetDifficulty);

        if (realImages.size() < batchCount || fakeImages.size() < batchCount) {
            throw new IllegalStateException("Not enough real/fake images to create paired batches.");
        }

        Collections.shuffle(realImages);
        Collections.shuffle(fakeImages);

        for (int batch = 1; batch <= batchCount; batch++) {
            imageGameRepository.save(new ImageGame(game, realImages.get(batch - 1), batch));
            imageGameRepository.save(new ImageGame(game, fakeImages.get(batch - 1), batch));
        }
    }

    private List<Image> fetchImagesByFakeness(Game game, boolean isFake, int needed, double difficulty) {
        List<Image> result = new ArrayList<>(imageRepository.findFreshByFakeness(isFake, PageRequest.of(0, needed)));

        if (result.size() < needed) {
            int remaining = needed - result.size();
            result.addAll(imageRepository.findPlayedByOthersByFakeness(game.getUser(), difficulty, isFake, PageRequest.of(0, remaining)));
        }

        if (result.size() < needed) {
            int remaining = needed - result.size();
            result.addAll(imageRepository.findPlayedByUserByFakeness(game.getUser(), difficulty, isFake, PageRequest.of(0, remaining)));
        }

        return result;
    }
}
