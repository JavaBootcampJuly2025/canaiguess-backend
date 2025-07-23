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
        NewGameRequestDTO request = new NewGameRequestDTO(3, 5, 2);
        User user = new User();
        NewGameResponseDTO expectedResponse = new NewGameResponseDTO("42");

        when(gameService.createGame(request, user)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<NewGameResponseDTO> response = gameController.createGame(request, user);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("42", response.getBody().getGameId());
        verify(gameService).createGame(request, user);
    }

    @Test
    void getGameByPublicId_shouldReturnGameInfo_whenGameExists() {
        String gameId = "1";
        User user = new User();
        GameDTO gameDto = new GameDTO();

        when(gameService.getGameByPublicId(gameId, user)).thenReturn(gameDto);

        ResponseEntity<GameDTO> response = gameController.getGameById(gameId, user);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(gameDto, response.getBody());
        verify(gameService).getGameByPublicId(gameId, user);
    }

    @Test
    void getNextBatch_shouldReturnImageBatchResponse() {
        String gameId = "1";
        User user = new User();

        // Create proper ImageDTO objects instead of empty constructors
        ImageDTO image1 = new ImageDTO("img1.jpg", "description1");
        ImageDTO image2 = new ImageDTO("img2.jpg", "description2");
        List<ImageDTO> images = Arrays.asList(image1, image2);

        when(gameSessionService.getNextBatchForGame(gameId, user)).thenReturn(images);

        ResponseEntity<ImageBatchResponseDTO> response = gameController.getNextBatch(gameId, user);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(images, response.getBody().getImages());
        verify(gameSessionService).getNextBatchForGame(gameId, user);
    }

    @Test
    void validateGuesses_shouldReturnGuessResults() {
        String gameId = "1";
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
}