package com.canaiguess.api.service;

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

@Service
public class GeminiService {
    private Client client;

    @PostConstruct
    public void init() {
        this.client = new Client(); // GEMINI_API_KEY is picked from env
    }

    public String analyzeImagePrompt(String imageUrl, String prompt) {
        try {
            Content input = Content.fromParts(
                    Part.fromText(prompt),
                    Part.fromUri(imageUrl, "image/jpeg")
            );

            GenerateContentResponse resp = client.models
                    .generateContent("gemini-2.5-pro-vision", input, null);

            return resp.text();
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
}

