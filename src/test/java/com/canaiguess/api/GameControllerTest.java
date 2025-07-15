package com.canaiguess.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createGame_asAnonymousUser_shouldSucceed() throws Exception {
        String requestJson = """
            {"gameMode":"single", "batches":3, "difficulty":2}
        """;
        mockMvc.perform(post("/api/game")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").isNotEmpty());
    }

    @Test
    void getGameById_nonExistingId_shouldReturnNotFound() throws Exception {
        String nonExistentId = "does-not-exist";
        mockMvc.perform(get("/api/game/" + nonExistentId))
                .andExpect(status().isNotFound());
    }


}

