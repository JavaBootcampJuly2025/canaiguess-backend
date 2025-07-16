package com.canaiguess.api.repository;

import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.ImageGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageGameRepository extends JpaRepository<ImageGame, Long> {

    // Get all image-game entries for a given game
    List<ImageGame> findByGame(Game game);

    // Get images for a specific batch of a game
    List<ImageGame> findByGameAndBatchNumber(Game game, int batchNumber);
}