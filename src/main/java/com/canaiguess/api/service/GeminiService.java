package com.canaiguess.api.service;

import com.canaiguess.api.dto.HintResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.List;

@Service
public class GeminiService {

    private final ModelGuessService modelGuessService;
    private Client client;

    private  static final List<String> models = List.of(
            "gemini-2.5-flash",
            "gemini-2.0-flash-lite",
            "gemini-2.0-flash",
            "gemini-2.5-flash-preview-tts",
            "gemini-2.5-pro-preview-tts"
    );

    private static final String PROMPT_TEXT = """
        Analyze the image. Is it AI-generated? Give 2–5 brief visual clues or signs. Be factual. Return a JSON response like:
        {
          "fake": true|false,
          "signs": ["reason 1", "reason 2", ...]
        }
        Only include 2–5 short (each maximum 20-words) visual signs or clues.
        Do NOT return anything outside the JSON object.
    """;

    public GeminiService(ModelGuessService modelGuessService) {
        this.modelGuessService = modelGuessService;
    }

    @PostConstruct
    public void init() {
        this.client = new Client(); // GOOGLE_API_KEY picked from env
    }

    public HintResponseDTO analyzeImagePrompt(String imageUrl) {
        try {
            byte[] imageBytes = fetchBytesFromUrl(imageUrl);

            Content input = Content.fromParts(
                    Part.fromText(PROMPT_TEXT),
                    Part.fromBytes(imageBytes, "image/jpeg")
            );

            ObjectMapper mapper = new ObjectMapper();
            HintResponseDTO dto = null;

            for (String model : models) {
                try {
                    GenerateContentResponse resp = client.models.generateContent(model, input, null);
                    if (resp == null || resp.text() == null) continue;

                    String json = extractJsonFromText(resp.text());
                    JsonNode root = mapper.readTree(json);

                    validateJsonResponse(root);
                    dto = mapper.treeToValue(root, HintResponseDTO.class);
                    break; // success

                } catch (Exception modelError) {
                    System.err.println("Model " + model + " failed: " + modelError.getMessage());
                    // continue to try next model
                }
            }

            if (dto == null) {
                throw new RuntimeException("All Gemini models failed or returned invalid responses.");
            }

            modelGuessService.storeModelGuessAsync(imageUrl, dto)
                .exceptionally(ex -> {
                    System.err.println("Failed to store model guess: {}" + ex.getMessage());
                    return null;
                });

            return dto;

        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze image: " + e.getMessage(), e);
        }
    }

    private byte[] fetchBytesFromUrl(String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
        return HttpClient.newHttpClient()
                .send(req, HttpResponse.BodyHandlers.ofByteArray())
                .body();
    }

    private String extractJsonFromText(String text) {
        if (text.startsWith("```")) {
            int start = text.indexOf("{");
            int end = text.lastIndexOf("}");
            if (start != -1 && end != -1 && end > start) {
                return text.substring(start, end + 1).trim();
            }
        }
        return text.trim();
    }

    private void validateJsonResponse(JsonNode root) {
        if (!root.has("fake") || !root.get("fake").isBoolean()) {
            throw new IllegalArgumentException("Missing or invalid 'fake' field.");
        }

        if (!root.has("signs") || !root.get("signs").isArray()) {
            throw new IllegalArgumentException("Missing or invalid 'signs' field.");
        }

        var signs = root.get("signs");
        if (signs.size() < 2 || signs.size() > 5) {
            throw new IllegalArgumentException("Invalid number of 'signs' elements.");
        }

        for (JsonNode sign : signs) {
            if (!sign.isTextual() || sign.asText().trim().isEmpty()) {
                throw new IllegalArgumentException("Empty 'sign' string encountered.");
            }
        }
    }

}
