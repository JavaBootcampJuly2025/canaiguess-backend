package com.canaiguess.api.repository;

import com.canaiguess.api.model.ModelGuess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelGuessRepository extends JpaRepository<ModelGuess, Long> {}
