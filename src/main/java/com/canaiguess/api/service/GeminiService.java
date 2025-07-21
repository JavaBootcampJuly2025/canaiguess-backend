package com.canaiguess.api.service;

import com.canaiguess.api.dto.HintResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

@Service
public class GeminiService {
    private Client client;

    @PostConstruct
    public void init() {
        this.client = new Client(); // GEMINI_API_KEY picked from env
    }

    public HintResponseDTO analyzeImagePrompt(String imageUrl) {
        try {
            byte[] imageBytes = fetchBytesFromUrl(imageUrl);

            Content input = Content.fromParts(
                    Part.fromText("""
                    Analyze the image. Is it AI-generated? If yes, give 2-5 brief visual clues or signs. Be factual. Return a JSON response like:
                    {
                      "fake": true|false,
                      "signs": ["reason 1", "reason 2", ...]
                    }
                    Only include 2–5 short (each about 20 words sentences) visual signs or clues.
                    """),
                    Part.fromBytes(imageBytes, "image/jpeg")
            );

            GenerateContentResponse resp = client.models
                    .generateContent("gemini-1.5-flash", input, null);

            String json = extractJsonFromMarkdown(Objects.requireNonNull(resp.text()));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, HintResponseDTO.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze image with Gemini: " + e.getMessage(), e);
        }
    }

    private byte[] fetchBytesFromUrl(String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
        return HttpClient.newHttpClient()
                .send(req, HttpResponse.BodyHandlers.ofByteArray())
                .body();
    }

    private String extractJsonFromMarkdown(String text) {
        if (text.startsWith("```")) {
            int start = text.indexOf("{");
            int end = text.lastIndexOf("}");
            if (start != -1 && end != -1 && end > start) {
                return text.substring(start, end + 1).trim();
            }
        }
        return text.trim();
    }

}
