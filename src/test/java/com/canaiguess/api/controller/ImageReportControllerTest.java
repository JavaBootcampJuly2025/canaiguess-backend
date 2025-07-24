package com.canaiguess.api.controller;

import com.canaiguess.api.config.JwtAuthenticationFilter;
import com.canaiguess.api.config.WithMockCustomUser;
import com.canaiguess.api.model.Image;
import com.canaiguess.api.model.ImageReport;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.ImageReportRepository;
import com.canaiguess.api.repository.ImageRepository;
import com.canaiguess.api.service.ImageReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.*;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;

@WebMvcTest(controllers = ImageReportController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
@Import(ImageReportControllerTest.TestConfig.class)
public class ImageReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageReportRepository imageReportRepository;

    @MockBean
    private ImageRepository imageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public ImageReportService imageReportService(ImageReportRepository imageReportRepository,
                                                     ImageRepository imageRepository) {
            return new ImageReportService(imageReportRepository, imageRepository);
        }
    }

    private User adminUser;

    @BeforeEach
    void setup() {
        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(com.canaiguess.api.enums.Role.ADMIN);
    }

    @Test
    @WithMockCustomUser
    void testGetUnresolvedReports() throws Exception {
        Image image = new Image();
        image.setPublicId("img123");
        image.setUrl("http://example.com/image.jpg");

        ImageReport report = new ImageReport();
        report.setId(1L);
        report.setImage(image);
        report.setUser(adminUser);
        report.setDescription("Bad content");
        report.setTitle("Inappropriate");
        report.setTimestamp(Instant.now());
        report.setResolved(false);

        when(imageReportRepository.findByResolvedFalseOrderByTimestampDesc())
                .thenReturn(List.of(report));

        mockMvc.perform(get("/api/reports/unresolved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reportId").value(1L))
                .andExpect(jsonPath("$[0].imageId").value("img123"))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].resolved").value(false));
    }

    @Test
    @WithMockCustomUser
    void testResolveReport() throws Exception {
        ImageReport report = new ImageReport();
        report.setId(1L);
        report.setUser(new User()); // dummy submitter
        report.setResolved(false);

        when(imageReportRepository.findById(1L)).thenReturn(Optional.of(report));

        mockMvc.perform(post("/api/reports/1/resolve"))
                .andExpect(status().isNoContent());

        ArgumentCaptor<ImageReport> captor = ArgumentCaptor.forClass(ImageReport.class);
        verify(imageReportRepository).save(captor.capture());

        ImageReport savedReport = captor.getValue();

        assertTrue(savedReport.isResolved(), "Report should be resolved");
        assertNotNull(savedReport.getReviewer(), "Reviewer should not be null");
        assertEquals("admin", savedReport.getReviewer().getUsername(), "Reviewer username should match");
    }

    @Test
    @WithMockCustomUser
    void testResolveReport_whenReportNotFound_thenReturn404() throws Exception {
        when(imageReportRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/reports/1/resolve"))
                .andExpect(status().isNotFound());
    }

}
