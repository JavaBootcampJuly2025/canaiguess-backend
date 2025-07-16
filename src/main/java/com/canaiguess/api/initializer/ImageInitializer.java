package com.canaiguess.api.initializer;

import com.canaiguess.api.model.Image;
import com.canaiguess.api.repository.ImageRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class ImageInitializer implements CommandLineRunner {

    private final ImageRepository imageRepository;

    public ImageInitializer(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (imageRepository.count() > 0) {
            return; // Skip if already populated
        }

        List<Image> images = new ArrayList<>();
        String[] domains = {
                "https://pub-45cdbc8aa7c94d018c9155c86ae133b3.r2.dev/", // AI (fake)
                "https://pub-fc94a80f6efd49889bdcbcd7b2c8a513.r2.dev/"  // Real
        };

        for (int i = 1; i <= 1000; i++) {
            String fileNameBase = String.format("%04d", i);

            for (int j = 0; j < 2; j++) {
                String domain = domains[j];
                boolean isAI = (j == 0);

                String fullUrl = checkFileExtension(domain, fileNameBase);
                if (fullUrl == null) { continue; }

                Image img = new Image();
                img.setFilename(fullUrl);
                img.setImage_type(isAI); // true = AI, false = real
                img.setTotal_guesses(0);
                img.setCorrect_guesses(0);
                images.add(img);
            }
        }

        imageRepository.saveAll(images);
    }

    private String checkFileExtension(String baseUrl, String fileNameBase) {
        String[] extensions = {"jpg", "png"};
        for (String ext : extensions) {
            String url = baseUrl + fileNameBase + "." + ext;
            if (urlExists(url)) {
                return url;
            }
        }
        return null;
    }

    private boolean urlExists(String urlStr) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            int responseCode = connection.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
