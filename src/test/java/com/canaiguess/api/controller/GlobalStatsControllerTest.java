package com.canaiguess.api.controller;

import com.canaiguess.api.config.JwtAuthenticationFilter;
import com.canaiguess.api.model.Image;
import com.canaiguess.api.repository.GameRepository;
import com.canaiguess.api.repository.ImageRepository;
import com.canaiguess.api.repository.ModelGuessRepository;
import com.canaiguess.api.repository.UserRepository;
import com.canaiguess.api.service.GlobalStatsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = GlobalStatsController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalStatsService.class)
public class GlobalStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageRepository imageRepository;

    @MockBean
    private ModelGuessRepository modelGuessRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GameRepository gameRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetGlobalStats() throws Exception {
        // Mock data
        Image img1 = new Image();
        img1.setPublicId("img1");
        img1.setUrl("http://img1");
        img1.setCorrect(10);
        img1.setTotal(20);

        Image img2 = new Image();
        img2.setPublicId("img2");
        img2.setUrl("http://img2");
        img2.setCorrect(5);
        img2.setTotal(30);

        List<Image> images = List.of(img1, img2);

        when(imageRepository.count()).thenReturn((long) images.size());
        when(imageRepository.findAll()).thenReturn(images);
        when(userRepository.count()).thenReturn(5L);
        when(gameRepository.count()).thenReturn(12L);

        // Expected values
        int totalCorrect = 15;
        int totalGuesses = 50;
        double expectedAccuracy = (double) totalCorrect / totalGuesses;

        mockMvc.perform(get("/api/global/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalImages").value(2))
                .andExpect(jsonPath("$.totalUsers").value(5))
                .andExpect(jsonPath("$.globalAccuracy").value(expectedAccuracy))
                .andExpect(jsonPath("$.hardestImageUrl").value("http://img2"))
                .andExpect(jsonPath("$.hardestImageAccuracy").value((double) 5 / 30))
                .andExpect(jsonPath("$.totalGamesPlayed").value(12));
    }

    @Test
    void testGetGlobalStats_NoImages() throws Exception {
        when(imageRepository.count()).thenReturn(0L);
        when(imageRepository.findAll()).thenReturn(List.of());
        when(userRepository.count()).thenReturn(0L);
        when(gameRepository.count()).thenReturn(0L);

        mockMvc.perform(get("/api/global/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalImages").value(0))
                .andExpect(jsonPath("$.totalUsers").value(0))
                .andExpect(jsonPath("$.globalAccuracy").value(0.0))
                .andExpect(jsonPath("$.hardestImageUrl").value(""))
                .andExpect(jsonPath("$.hardestImageAccuracy").value(0.0))
                .andExpect(jsonPath("$.totalGamesPlayed").value(0));
    }

    @Test
    void testGetGlobalStats_ImagesWithNoGuesses() throws Exception {
        Image img1 = new Image();
        img1.setPublicId("img1");
        img1.setUrl("http://img1");
        img1.setCorrect(0);
        img1.setTotal(0);

        Image img2 = new Image();
        img2.setPublicId("img2");
        img2.setUrl("http://img2");
        img2.setCorrect(0);
        img2.setTotal(0);

        List<Image> images = List.of(img1, img2);

        when(imageRepository.count()).thenReturn((long) images.size());
        when(imageRepository.findAll()).thenReturn(images);
        when(userRepository.count()).thenReturn(2L);
        when(gameRepository.count()).thenReturn(3L);

        mockMvc.perform(get("/api/global/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalImages").value(2))
                .andExpect(jsonPath("$.totalUsers").value(2))
                .andExpect(jsonPath("$.globalAccuracy").value(0.0))
                .andExpect(jsonPath("$.hardestImageUrl").value(""))
                .andExpect(jsonPath("$.hardestImageAccuracy").value(0.0))
                .andExpect(jsonPath("$.totalGamesPlayed").value(3));
    }

    @Test
    void testGetGlobalStats_PerfectAccuracy() throws Exception {
        Image img = new Image();
        img.setPublicId("perfect");
        img.setUrl("http://perfect.img");
        img.setCorrect(10);
        img.setTotal(10);

        when(imageRepository.count()).thenReturn(1L);
        when(imageRepository.findAll()).thenReturn(List.of(img));
        when(userRepository.count()).thenReturn(1L);
        when(gameRepository.count()).thenReturn(1L);

        mockMvc.perform(get("/api/global/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalImages").value(1))
                .andExpect(jsonPath("$.globalAccuracy").value(1.0))
                .andExpect(jsonPath("$.hardestImageUrl").value("http://perfect.img"))
                .andExpect(jsonPath("$.hardestImageAccuracy").value(1.0))
                .andExpect(jsonPath("$.totalGamesPlayed").value(1));
    }


}
