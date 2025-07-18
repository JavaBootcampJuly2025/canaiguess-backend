package com.canaiguess.api.repository;

import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByUserIdOrderByCreatedAtDesc(User user, Pageable pageable);
}
