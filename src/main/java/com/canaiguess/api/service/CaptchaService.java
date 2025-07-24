package com.canaiguess.api.service;

import com.canaiguess.api.dto.CaptchaRequestDTO;
import com.canaiguess.api.dto.CaptchaResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class CaptchaService {
    // Google reCAPTCHA verification endpoint
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    private final String secretKey;

    public CaptchaService(@Value("${captcha.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * Calls Google's reCAPTCHA siteverify endpoint and parses the success field.
     *
     * @param request contains the client captcha token from frontend
     * @return DTO with 'success' field true/false
     */
    public CaptchaResponseDTO verifyCaptcha(CaptchaRequestDTO request) {
        RestTemplate restTemplate = new RestTemplate();

        // Build the POST parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", secretKey);
        params.add("response", request.getToken());

        CaptchaResponseDTO captchaResponse = new CaptchaResponseDTO();
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Send POST request to Google reCAPTCHA siteverify
            String response = restTemplate.postForObject(VERIFY_URL, params, String.class);
            JsonNode rootNode = mapper.readTree(response);
            boolean success = rootNode.path("success").asBoolean(false);
            captchaResponse.setSuccess(success);
        } catch (RestClientException | JsonProcessingException e) {
            // Add loging later, fallback to failed verification
            // logger.error("CAPTCHA verification failed", e);
            captchaResponse.setSuccess(false);
        }

        return captchaResponse;
    }
}
