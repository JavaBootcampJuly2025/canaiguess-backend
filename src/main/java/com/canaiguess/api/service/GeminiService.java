package com.canaiguess.api.service;

import com.canaiguess.api.dto.HintResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import javax.naming.ServiceUnavailableException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
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


            List<String> models = List.of(
                    "gemini-2.5-flash-lite-preview-06-17",
                    "gemini-2.5-flash",
                    "gemini-2.0-flash-lite",
                    "gemini-2.0-flash",
                    "gemini-2.5-flash-preview-tts",
                    "gemini-2.5-pro-preview-tts"
            );

            GenerateContentResponse resp = null;
            Exception lastError = null;

            for (String model : models) {
                try {
                    resp = client.models.generateContent(model, input, null);
                    if (resp != null) {
                        break; // success
                    }
                } catch (Exception e) {
                    lastError = e;
                    System.err.println("Model " + model + " failed: " + e.getMessage());
                }
            }

            if (resp == null) {
                throw new RuntimeException("All Gemini models failed", lastError);
            }

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
