package com.canaiguess.api.service;

import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.Image;
import com.canaiguess.api.model.ImageGame;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.GameRepository;
import com.canaiguess.api.repository.ImageGameRepository;
import com.canaiguess.api.repository.ImageRepository;
import com.canaiguess.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageGameService {

    private final ImageRepository imageRepository;
    private final ImageGameRepository imageGameRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public ImageGameService(ImageRepository imageRepository,
                            ImageGameRepository imageGameRepository,
                            GameRepository gameRepository,
                            UserRepository userRepository) {

        this.imageRepository = imageRepository;
        this.imageGameRepository = imageGameRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public void allocateImagesForGame(Game game) {
        int totalNeeded = game.getBatchCount() * game.getBatchSize();
        double targetDifficulty = game.getDifficulty() / 100.0; // assume 0-100 input

        // unplayed images for this user
        List<Image> unplayed = imageRepository.findUnplayedImagesByUser(game.getUserId());

        // rank by distance to target difficulty
        List<Image> sortedByDifficulty = unplayed.stream()
                .filter(img -> img.getTotal() > 0)
                .sorted((a, b) -> {
                    double da = 1.0 - (a.getCorrect() / (double) a.getTotal());
                    double db = 1.0 - (b.getCorrect() / (double) b.getTotal());
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
                    .filter(img -> img.getTotal() > 0)
                    .sorted((a, b) -> {
                        double da = 1.0 - (a.getCorrect() / (double) a.getTotal());
                        double db = 1.0 - (b.getCorrect() / (double) b.getTotal());
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

    public List<Boolean> validateGuesses(List<String> imageUrls, List<Boolean> guesses) {
        if (imageUrls.size() != guesses.size()) {
            throw new IllegalArgumentException("Mismatched image and guess count");
        }

        List<Boolean> correct = new ArrayList<>();

        for (int i = 0; i < imageUrls.size(); i++) {
            String url = imageUrls.get(i);
            boolean userGuess = guesses.get(i);

            Image image = imageRepository.findByFilename(url)
                    .orElseThrow(() -> new RuntimeException("Image not found"));

            boolean isAI = image.isFake();

            // update statistics
            image.setTotal(image.getTotal() + 1);
            if (userGuess == isAI) {
                image.setCorrect(image.getCorrect() + 1);
                correct.add(true);
            } else {
                correct.add(false);
            }

            imageRepository.save(image);
        }

        return correct;
    }

    public List<String> getNextBatchForGame(long gameId, long userId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (game.getUserId() != userId) {
            throw new RuntimeException("Unauthorized access to game");
        }

        int currentBatch = game.getCurrentBatch();

        if (currentBatch > game.getBatchCount()) {

            // update user points only and end the game
            finalizeGameAndUpdateUserPoints(game);

            return List.of(); // empty batch <=> game finished
        }

        List<ImageGame> imageGames = imageGameRepository.findByGameAndBatchNumber(game, currentBatch);
        if (imageGames.isEmpty()) {
            throw new RuntimeException("No images found for current batch");
        }

        game.setCurrentBatch(currentBatch + 1);
        gameRepository.save(game);

        return imageGames.stream()
                .map(ig -> ig.getImage().getFilename())
                .toList();
    }

    public void finalizeGameAndUpdateUserPoints(Game game) {
        List<ImageGame> imageGames = imageGameRepository.findByGame(game);

        long correctGuesses = imageGames.stream()
                .filter(ImageGame::isUserGuessedCorrectly)
                .count();

        User user = userRepository.findById(game.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setScore(user.getScore() + (int) correctGuesses);
        userRepository.save(user);

        System.out.println("User got " + correctGuesses + " correct guesses");

        game.setFinished(true);
        gameRepository.save(game);
    }




}

