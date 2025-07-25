package com.canaiguess.api.controller;

import com.canaiguess.api.config.JwtAuthenticationFilter;
import com.canaiguess.api.dto.CaptchaRequestDTO;
import com.canaiguess.api.dto.CaptchaResponseDTO;
import com.canaiguess.api.service.CaptchaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CaptchaController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
// Different approach from AuthenticationControllerTest, now testing both controller and service at once
class CaptchaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void setUpServer() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDownServer() throws Exception {
        mockWebServer.shutdown();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CaptchaService captchaService() {
            return new CaptchaService("test-secret") {
                @Override
                public String getVerificationUrl() {
                    return mockWebServer.url("/siteverify").toString(); // override endpoint
                }
            };
        }
    }

    @Test
    void testValidCaptchaReturnsSuccessTrue() throws Exception {
        // Mock Google's API response
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"success\": true}")
                .addHeader("Content-Type", "application/json"));

        CaptchaRequestDTO request = new CaptchaRequestDTO();
        request.setToken("valid-token");

        String json = objectMapper.writeValueAsString(request);

        String responseBody = mockMvc.perform(post("/api/v1/captcha/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        CaptchaResponseDTO response = objectMapper.readValue(responseBody, CaptchaResponseDTO.class);
        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    void testInvalidCaptchaReturnsSuccessFalse() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"success\": false}")
                .addHeader("Content-Type", "application/json"));

        CaptchaRequestDTO request = new CaptchaRequestDTO();
        request.setToken("invalid-token");

        String json = objectMapper.writeValueAsString(request);

        String responseBody = mockMvc.perform(post("/api/v1/captcha/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        CaptchaResponseDTO response = objectMapper.readValue(responseBody, CaptchaResponseDTO.class);
        assertThat(response.isSuccess()).isFalse();
    }
}
