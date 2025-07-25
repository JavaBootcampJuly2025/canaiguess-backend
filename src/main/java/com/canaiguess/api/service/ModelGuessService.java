package com.canaiguess.api.service;

import com.canaiguess.api.dto.HintResponseDTO;
import com.canaiguess.api.exception.ModelGuessStorageException;
import com.canaiguess.api.model.ModelGuess;
import com.canaiguess.api.repository.ModelGuessRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ModelGuessService {

    private final ModelGuessRepository modelGuessRepository;

    @Async
    @Transactional
    public CompletableFuture<Void> storeModelGuessAsync(String imageUrl, HintResponseDTO dto) {
        try {
            ModelGuess guess = ModelGuess.builder()
                    .imageUrl(imageUrl)
                    .fake(dto.isFake())
                    .signs(dto.getSigns())
                    .build();

            modelGuessRepository.save(guess);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(
                    new ModelGuessStorageException("Failed to store model guess for image: " + imageUrl, e)
            );
            return failed;
        }
    }


}

