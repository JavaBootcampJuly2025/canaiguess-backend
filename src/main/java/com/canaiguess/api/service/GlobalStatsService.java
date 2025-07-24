package com.canaiguess.api.service;

import com.canaiguess.api.dto.GlobalStatsDTO;
import com.canaiguess.api.model.Image;
import com.canaiguess.api.repository.ImageRepository;
import com.canaiguess.api.repository.ModelGuessRepository;
import com.canaiguess.api.repository.UserRepository;
import com.canaiguess.api.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class GlobalStatsService
{

    private final ImageRepository imageRepository;
    private final ModelGuessRepository modelGuessRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public GlobalStatsService(
            ImageRepository imageRepository,
            ModelGuessRepository modelGuessRepository,
            UserRepository userRepository,
            GameRepository gameRepository) {
        this.imageRepository = imageRepository;
        this.modelGuessRepository = modelGuessRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public GlobalStatsDTO getGlobalStats()
    {
        int totalImages = (int) imageRepository.count();
        long totalUsers = userRepository.count();
        long totalGamesPlayed = gameRepository.count();

        List<Image> images = imageRepository.findAll();

        int totalCorrect = images.stream().mapToInt(Image::getCorrect).sum();
        int totalGuesses = images.stream().mapToInt(Image::getTotal).sum();

        double globalAccuracy = totalGuesses > 0 ? ((double) totalCorrect / totalGuesses) : 0.0;

        // Find hardest image (lowest accuracy)
        Image hardestImage = images.stream()
                .filter(img -> img.getTotal() > 0)
                .min(Comparator.comparingDouble(img -> (double) img.getCorrect() / img.getTotal()))
                .orElse(null);

        String hardestImageId = hardestImage != null ? hardestImage.getPublicId() : null;
        double hardestImageAccuracy = hardestImage != null && hardestImage.getTotal() > 0
                ? (double) hardestImage.getCorrect() / hardestImage.getTotal()
                : 0.0;

        // Total hints taken - sum all `signs` lists in ModelGuess
        int totalHintsTaken = modelGuessRepository.findAll().stream()
                .mapToInt(mg -> mg.getSigns() != null ? mg.getSigns().size() : 0)
                .sum();

        return GlobalStatsDTO.builder()
                .totalImages(totalImages)
                .globalAccuracy(globalAccuracy)
                .totalUsers((int) totalUsers)
                .hardestImageId(hardestImageId)
                .hardestImageAccuracy(hardestImageAccuracy)
                .totalHintsTaken(totalHintsTaken)
                .totalGamesPlayed(totalGamesPlayed)
                .build();
    }
}
