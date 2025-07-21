package com.canaiguess.api.controller;

import com.canaiguess.api.dto.*;
import com.canaiguess.api.model.User;
import com.canaiguess.api.service.GameService;
import com.canaiguess.api.service.GameSessionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameControllerTest {

    @Mock
    private GameService gameService;

    @Mock
    private GameSessionService gameSessionService;

    @InjectMocks
    private GameController gameController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createGame_shouldReturnNewGameResponse() {
        // Arrange
        NewGameRequestDTO request = new NewGameRequestDTO(3, 5, 2); // Example values
        User user = new User(); // Could populate fields if needed
        NewGameResponseDTO expectedResponse = new NewGameResponseDTO(42L);

        when(gameService.createGame(request, user)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<NewGameResponseDTO> response = gameController.createGame(request, user);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(42L, response.getBody().getGameId());
        verify(gameService).createGame(request, user);
    }

    @Test
    void getGameById_shouldReturnGameInfo_whenGameExists() {
        Long gameId = 1L;
        GameInfoResponseDTO gameInfo = new GameInfoResponseDTO();

        when(gameService.getGameById(gameId)).thenReturn(gameInfo);

        ResponseEntity<GameInfoResponseDTO> response = gameController.getGameById(gameId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(gameInfo, response.getBody());
        verify(gameService).getGameById(gameId);
    }

    @Test
    void getGameById_shouldReturnNotFound_whenGameDoesNotExist() {
        Long gameId = 1L;

        when(gameService.getGameById(gameId)).thenReturn(null);

        ResponseEntity<GameInfoResponseDTO> response = gameController.getGameById(gameId);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(gameService).getGameById(gameId);
    }

    @Test
    void getNextBatch_shouldReturnImageBatchResponse() {
        Long gameId = 1L;
        User user = new User();
        List<String> imageUrls = Arrays.asList("img1.jpg", "img2.jpg");

        when(gameSessionService.getNextBatchForGame(gameId, user)).thenReturn(imageUrls);

        ResponseEntity<ImageBatchResponseDTO> response = gameController.getNextBatch(gameId, user);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(imageUrls, response.getBody().getImages());
        verify(gameSessionService).getNextBatchForGame(gameId, user);
    }

    @Test
    void validateGuesses_shouldReturnGuessResults() {
        Long gameId = 1L;
        User user = new User();
        List<Boolean> guesses = Arrays.asList(Boolean.TRUE, Boolean.FALSE);
        GuessRequestDTO guessRequest = new GuessRequestDTO(guesses);
        List<Boolean> results = Arrays.asList(Boolean.TRUE, Boolean.FALSE);

        when(gameSessionService.validateGuesses(gameId, user, guessRequest.getGuesses())).thenReturn(results);

        ResponseEntity<GuessResultDTO> response = gameController.validateGuesses(gameId, guessRequest, user);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(results, response.getBody().getCorrect());
        verify(gameSessionService).validateGuesses(gameId, user, guessRequest.getGuesses());
    }

    @Test
    void getGameResults_shouldReturnGameResults() {
        Long gameId = 1L;
        User user = new User();
        GameResultsDTO results = new GameResultsDTO(3, 1, 75, 3); // assuming constructor is (correct, incorrect, score, total)

        when(gameService.getGameResults(gameId, user)).thenReturn(results);

        ResponseEntity<GameResultsDTO> response = gameController.getGameResults(gameId, user);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(results, response.getBody());
        verify(gameService).getGameResults(gameId, user);
    }
}